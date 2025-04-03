package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientFundsException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import com.example.virtualwallet.repositories.TransactionRepository;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.Helpers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTests {

    public static final String RECIPIENT = "Recipient";
    public static final String TEST_DESCRIPTION = "Test description";
    public static final String VALUE = "100";
    public static final String VALUE_2 = "30";
    public static final String SENDER = "Sender";
    public static final String RECIPIENT_MAIL = "user2@x.com";
    public static final int VALUE_3 = 70;
    public static final String TRANSACTION = "Transaction";

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private User user;
    private Wallet wallet;
    private Wallet recipientWallet;

    @BeforeEach
    void setup() {
        user = createMockUserWithoutCardsAndWallets();
        wallet = createMockWallet(user);
        recipientWallet = createMockWallet(createMockUserWithoutCardsAndWallets());
    }

    @Test
    void getTransactionById_Admin_Success() {
        User admin = createMockAdmin();
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());

        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(transactionRepository.findById(tx.getId())).thenReturn(Optional.of(tx));

        assertEquals(tx.getId(), transactionService.getTransactionById(tx.getId()).getTransactionId());
    }

    @Test
    void getTransactionById_Admin_NotFound_Throws() {
        User admin = createMockAdmin();
        UUID id = UUID.randomUUID();
        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionById(id));
    }

    @Test
    void getTransactionById_User_Success() {
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(transactionRepository.findByIdAndUsername(tx.getId(), user.getUsername())).thenReturn(Optional.of(tx));

        assertEquals(tx.getId(), transactionService.getTransactionById(tx.getId()).getTransactionId());
    }

    @Test
    void getTransactionById_User_NotFound_Throws() {
        UUID id = UUID.randomUUID();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(transactionRepository.findByIdAndUsername(id, user.getUsername())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionById(id));
    }

    @Test
    void createTransaction_Valid_Success() {
        TransactionInput input = createMockTransactionInput();
        User recipientUser = createMockAdmin();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(userService.findUserByUsernameOrEmailOrPhoneNumber(anyString())).thenReturn(recipientUser);
        when(walletService.checkIfUserHasActiveWalletWithCurrency(any(), any())).thenReturn(true);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(user.getUsername()), any())).thenReturn(wallet);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(recipientUser.getUsername()), any())).thenReturn(recipientWallet);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> transactionService.createTransaction(input));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_Valid_Success_With_No_Description() {
        TransactionInput input = createMockTransactionInput();
        input.setDescription(null);
        User recipientUser = createMockAdmin();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(userService.findUserByUsernameOrEmailOrPhoneNumber(anyString())).thenReturn(recipientUser);
        when(walletService.checkIfUserHasActiveWalletWithCurrency(any(), any())).thenReturn(true);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(user.getUsername()), any())).thenReturn(wallet);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(recipientUser.getUsername()), any())).thenReturn(recipientWallet);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TransactionOutput output = assertDoesNotThrow(() -> transactionService.createTransaction(input));
        verify(transactionRepository).save(any(Transaction.class));
        assertNull(input.getDescription());
        assertEquals(TRANSACTION, output.getDescription());
    }

    @Test
    void createTransaction_SendingToSelf_Throws() {
        TransactionInput input = createMockTransactionInput();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(userService.findUserByUsernameOrEmailOrPhoneNumber(any())).thenReturn(user);

        assertThrows(InvalidUserInputException.class, () -> transactionService.createTransaction(input));
    }

    @Test
    void createTransaction_NoWallet_Throws() {
        TransactionInput input = createMockTransactionInput();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(userService.findUserByUsernameOrEmailOrPhoneNumber(any())).thenReturn(createMockAdmin());
        when(walletService.checkIfUserHasActiveWalletWithCurrency(any(), any())).thenReturn(false);

        assertThrows(InvalidUserInputException.class, () -> transactionService.createTransaction(input));
    }

    @Test
    void createTransaction_NotEnoughFunds_Throws_InsufficientFundsException() {
        wallet.setBalance(BigDecimal.TEN);
        TransactionInput input = new TransactionInput(RECIPIENT_MAIL,
                new BigDecimal(VALUE), USD, "");
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(userService.findUserByUsernameOrEmailOrPhoneNumber(any())).thenReturn(createMockAdmin());
        when(walletService.checkIfUserHasActiveWalletWithCurrency(any(), any())).thenReturn(true);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(user.getUsername()), any())).thenReturn(wallet);

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(input));
    }

    @Test
    void createTransactionMVC_Valid_Success() {
        user.setUsername(SENDER);
        User recipient = createMockUserWithoutCardsAndWallets();
        recipient.setUsername(RECIPIENT);
        wallet.setBalance(new BigDecimal(VALUE));

        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(RECIPIENT), any(Currency.class))).thenReturn(recipientWallet);

        assertDoesNotThrow(() -> transactionService.createTransactionMVC(user, recipient, wallet,
                new BigDecimal(VALUE_2), TEST_DESCRIPTION));
        verify(transactionRepository).save(any(Transaction.class));
        assertEquals(new BigDecimal(VALUE_3), wallet.getBalance());
        verify(walletService).getOrCreateWalletByUsernameAndCurrency(eq(RECIPIENT), any(Currency.class));
    }

    @Test
    void createTransactionMVC_Valid_Success_With_No_Description() {
        user.setUsername(SENDER);
        User recipient = createMockUserWithoutCardsAndWallets();
        recipient.setUsername(RECIPIENT);
        wallet.setBalance(new BigDecimal(VALUE));

        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(RECIPIENT), any(Currency.class))).thenReturn(recipientWallet);

        transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        assertDoesNotThrow(() -> transactionService.createTransactionMVC(user, recipient, wallet,
                new BigDecimal(VALUE_2), null));

        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(TRANSACTION, savedTransaction.getDescription());

    }

    @Test
    void createTransactionMVC_SendingToSelf_Throws() {
        assertThrows(InvalidUserInputException.class, () ->
                transactionService.createTransactionMVC(user, user, wallet, BigDecimal.ONE, TEST_DESCRIPTION));
    }

    @Test
    void createTransactionMVC_NotEnoughFunds_Throws() {
        wallet.setBalance(new BigDecimal("10"));
        User recipient = createMockUserWithoutCardsAndWallets();
        recipient.setUsername(RECIPIENT);

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.createTransactionMVC(user, recipient, wallet, new BigDecimal(VALUE),
                        TEST_DESCRIPTION));
    }

    @Test
    void filterTransactions_ReturnsResults() {
        TransactionFilterOptions filters = createMockTransactionFilterOptions();

        Page<Transaction> page = new PageImpl<>(List.of(new Transaction()));
        when(transactionRepository.findAll((Specification<Transaction>) any(), any(Pageable.class))).thenReturn(page);

        assertEquals(1, transactionService.filterTransactions(filters).size());
    }

    @Test
    void filterTransactions_ReturnsEmptyList() {
        TransactionFilterOptions filters = createMockTransactionFilterOptions();

        Page<Transaction> emptyPage = new PageImpl<>(List.of());
        when(transactionRepository.findAll((Specification<Transaction>) any(), any(Pageable.class))).thenReturn(emptyPage);

        assertTrue(transactionService.filterTransactions(filters).isEmpty());
    }

    @Test
    void filterTransactionsPage_ReturnsPage() {
        TransactionFilterOptions filters = createMockTransactionFilterOptions();

        Page<Transaction> page = new PageImpl<>(List.of(new Transaction()));
        when(transactionRepository.findAll((Specification<Transaction>) any(), any(Pageable.class))).thenReturn(page);

        assertEquals(1, transactionService.filterTransactionsPage(filters).getContent().size());
    }
}
