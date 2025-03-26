package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.transfer.*;
import com.example.virtualwallet.models.enums.TransferStatus;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import com.example.virtualwallet.repositories.TransferRepository;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.Helpers.*;
import static com.example.virtualwallet.services.TransferServiceImpl.EXPIRED_CARD;
import static com.example.virtualwallet.services.TransferServiceImpl.NOT_CARD_OWNER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTests {

    public static final String AMOUNT = "100";
    public static final String AMOUNT_2 = "50";
    public static final String VALUE = "200";
    public static final String MOCK_WITHDRAW_API_ERROR = "Mock Withdraw API error";
    public static final String API_ERROR = "API Error";
    public static final String VALUE_2 = "150";
    @Mock
    private TransferRepository transferRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserService userService;

    @Mock
    private CardService cardService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TransferServiceImpl transferService;

    private User user;
    private Card validCard;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = createMockUserWithCardsAndWallets();
        validCard = user.getCards().iterator().next();
        wallet = user.getWallets().iterator().next();
        wallet.setBalance(BigDecimal.ZERO);
    }

    @Test
    void processTransfer_ApprovedStatus_UpdatesWalletBalance() {
        TransferInput input = createMockTransferInput(validCard.getId(), AMOUNT);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.getCardById(validCard.getId())).thenReturn(validCard);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(anyString(), any()))
                .thenReturn(wallet);
        when(restTemplate.getForEntity(anyString(), eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(true));
        when(transferRepository.save(any())).thenAnswer(inv -> {
            Transfer t = inv.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        FullTransferInfoOutput result = transferService.processTransfer(input);

        assertEquals(TransferStatus.APPROVED.toString(), result.getStatus());
        assertEquals(new BigDecimal(AMOUNT), wallet.getBalance());
        verify(walletService).update(wallet);
    }

    @Test
    void processTransfer_DeclinedStatus_DoesUpdateWallet() {
        TransferInput input = createMockTransferInput(validCard.getId(), AMOUNT_2);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.getCardById(validCard.getId())).thenReturn(validCard);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(anyString(), any()))
                .thenReturn(wallet);
        when(restTemplate.getForEntity(anyString(), eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(false));
        when(transferRepository.save(any())).thenAnswer(inv -> {
            Transfer t = inv.getArgument(0);
            t.setId(UUID.randomUUID());
            t.setStatus(TransferStatus.DECLINED);
            return t;
        });

        FullTransferInfoOutput result = transferService.processTransfer(input);

        assertEquals(TransferStatus.DECLINED.toString(), result.getStatus());
        verify(walletService, never()).update(any());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void processTransfer_ExpiredCard_ThrowsException() {
        Card expiredCard = createMockExpiredCard(user);
        TransferInput input = createMockTransferInput(expiredCard.getId(), AMOUNT);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.getCardById(expiredCard.getId())).thenReturn(expiredCard);

        InvalidUserInputException exception = assertThrows(InvalidUserInputException.class,
                () -> transferService.processTransfer(input));

        assertEquals(EXPIRED_CARD, exception.getMessage());
    }

    @Test
    void processTransfer_NotCardOwner_ThrowsException() {
        User otherUser = createMockUserWithoutCardsAndWallets();
        Card otherUserCard = createMockCard(otherUser);
        TransferInput input = createMockTransferInput(otherUserCard.getId(), AMOUNT);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.getCardById(otherUserCard.getId())).thenReturn(otherUserCard);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> transferService.processTransfer(input));

        assertEquals(NOT_CARD_OWNER, exception.getMessage());
    }

    @Test
    void getTransferById_AdminAccess_ReturnsTransfer() {
        User admin = createMockAdmin();
        Transfer transfer = createMockTransfer(validCard, wallet);

        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(transferRepository.findById(transfer.getId())).thenReturn(Optional.of(transfer));

        FullTransferInfoOutput result = transferService.getTransferById(transfer.getId());

        assertEquals(transfer.getId(), result.getTransferId());
    }

    @Test
    void getTransferById_AdminAccess_TransferNotFound_ThrowsException() {
        User admin = createMockAdmin();
        UUID id = UUID.randomUUID();

        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(transferRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> transferService.getTransferById(id));
    }

    @Test
    void getTransferById_UserAccess_ValidatesOwnership() {
        Transfer transfer = createMockTransfer(validCard, wallet);
        transfer.setRecipientUsername(user.getUsername());

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(transferRepository.findByIdAndUsername(transfer.getId(), user.getUsername()))
                .thenReturn(Optional.of(transfer));

        FullTransferInfoOutput result = transferService.getTransferById(transfer.getId());

        assertEquals(transfer.getId(), result.getTransferId());
    }

    @Test
    void getTransferById_UserAccess_TransferNotFound_ThrowsException() {
        UUID id = UUID.randomUUID();

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(transferRepository.findByIdAndUsername(id, user.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> transferService.getTransferById(id));
    }

    @Test
    void filterTransfers_ValidFilters_ReturnsPaginatedResults() {
        TransferFilterOptions filters = new TransferFilterOptions();
        filters.setPage(0);
        filters.setSize(10);
        filters.setSortBy(DATE);
        filters.setSortOrder(SORT_ORDER);

        Transfer mockTransfer = createMockTransfer(validCard, wallet);
        Page<Transfer> mockPage = new PageImpl<>(List.of(mockTransfer));

        when(transferRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        TransfersPage result = transferService.filterTransfers(filters);

        assertEquals(1, result.getTransfers().size());
        assertEquals(mockTransfer.getId(), result.getTransfers().get(0).getTransferId());
    }

    @Test
    void filterTransfers_NoResults_ReturnsEmptyPage() {
        TransferFilterOptions filters = new TransferFilterOptions();
        filters.setPage(0);
        filters.setSize(10);
        filters.setSortBy(DATE);
        filters.setSortOrder(SORT_ORDER);

        Page<Transfer> emptyPage = new PageImpl<>(List.of());

        when(transferRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        TransfersPage result = transferService.filterTransfers(filters);

        assertTrue(result.getTransfers().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void processTransferMVC_ApprovedStatus_UpdatesWallet() {
        TransferInputMVC input = new TransferInputMVC();
        input.setCardId(validCard.getId());
        input.setWalletId(wallet.getId());
        input.setAmount(new BigDecimal(VALUE));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.getCardById(validCard.getId())).thenReturn(validCard);
        when(walletService.getWalletById(wallet.getId())).thenReturn(wallet);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(user.getUsername()), any()))
                .thenReturn(wallet);
        when(restTemplate.getForEntity(anyString(), eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(true));
        when(transferRepository.save(any())).thenAnswer(inv -> {
            Transfer t = inv.getArgument(0);
            t.setId(UUID.randomUUID());
            t.setStatus(TransferStatus.APPROVED);
            return t;
        });

        FullTransferInfoOutput result = transferService.processTransfer(input);

        assertEquals(TransferStatus.APPROVED.toString(), result.getStatus());
        verify(walletService).update(wallet);
        assertEquals(new BigDecimal(VALUE), wallet.getBalance());
    }

    @Test
    void callMockWithdrawApi_Error_ThrowsRuntimeException() {
        when(restTemplate.getForEntity(anyString(), eq(Boolean.class)))
                .thenThrow(new RuntimeException(API_ERROR));

        TransferInput input = createMockTransferInput(validCard.getId(), AMOUNT);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.getCardById(validCard.getId())).thenReturn(validCard);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transferService.processTransfer(input));

        assertTrue(exception.getMessage().contains(MOCK_WITHDRAW_API_ERROR));
    }

    @Test
    void processTransferMVC_DeclinedStatus_DoesNotUpdateWallet() {
        TransferInputMVC input = new TransferInputMVC();
        input.setCardId(validCard.getId());
        input.setWalletId(wallet.getId());
        input.setAmount(new BigDecimal(VALUE_2));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.getCardById(validCard.getId())).thenReturn(validCard);
        when(walletService.getWalletById(wallet.getId())).thenReturn(wallet);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(eq(user.getUsername()), any()))
                .thenReturn(wallet);
        when(restTemplate.getForEntity(anyString(), eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(false));
        when(transferRepository.save(any())).thenAnswer(inv -> {
            Transfer t = inv.getArgument(0);
            t.setId(UUID.randomUUID());
            t.setStatus(TransferStatus.DECLINED);
            return t;
        });

        FullTransferInfoOutput result = transferService.processTransfer(input);

        assertEquals(TransferStatus.DECLINED.toString(), result.getStatus());
        verify(walletService, never()).update(wallet);
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

}
