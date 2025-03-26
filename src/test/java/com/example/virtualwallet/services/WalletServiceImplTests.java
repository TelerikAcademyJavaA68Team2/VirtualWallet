package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.dtos.wallet.WalletsWithHistoryOutput;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import com.example.virtualwallet.repositories.WalletRepository;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import com.example.virtualwallet.services.contracts.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static com.example.virtualwallet.Helpers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTests {

    public static final String VALUE = "25.00";
    @Mock
    private WalletRepository walletRepository;
    @Mock private ExchangeRateService exchangeRateService;
    @Mock private UserService userService;

    @InjectMocks private WalletServiceImpl walletService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setup() {
        user = createMockUserWithoutCardsAndWallets();
        wallet = createMockWallet(user);
    }

    @Test
    void getWalletById_Success() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        Wallet result = walletService.getWalletById(wallet.getId());
        assertEquals(wallet.getId(), result.getId());
    }

    @Test
    void getWalletById_NotFound_Throws() {
        UUID id = UUID.randomUUID();
        when(walletRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> walletService.getWalletById(id));
    }

    @Test
    void getOrCreateWalletByUsernameAndCurrency_CreatesNew() {
        when(walletRepository.findByUsernameAndCurrency(user.getUsername(), Currency.USD)).thenReturn(Optional.empty());
        when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);
        walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(), Currency.USD);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void getOrCreateWalletByUsernameAndCurrency_RestoresDeleted() {
        wallet.markAsDeleted();
        when(walletRepository.findByUsernameAndCurrency(user.getUsername(), Currency.USD)).thenReturn(Optional.of(wallet));
        walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(), Currency.USD);
        assertFalse(wallet.isDeleted());
        verify(walletRepository).save(wallet);
    }

    @Test
    void getActiveWalletsOfUser_ReturnsList() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.findActiveWalletsByUserId(userId)).thenReturn(List.of(wallet));
        List<Wallet> result = walletService.getActiveWalletsOfUser(userId);
        assertEquals(1, result.size());
    }

    @Test
    void getActiveUserWalletsDto_ReturnsList() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.findActiveWalletsByUserId(user.getId())).thenReturn(List.of(wallet));
        assertEquals(1, walletService.getActiveUserWalletsDto().size());
    }

    @Test
    void softDeleteAuthenticatedUserWalletById_Success() {
        wallet.setBalance(BigDecimal.ZERO);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        walletService.softDeleteAuthenticatedUserWalletById(wallet.getId());
        assertTrue(wallet.isDeleted());
        verify(walletRepository).save(wallet);
    }

    @Test
    void softDeleteAuthenticatedUserWalletById_NotOwner_Throws() {
        User otherUser = createMockUserWithoutCardsAndWallets();
        wallet.setOwner(otherUser);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        assertThrows(UnauthorizedAccessException.class, () -> walletService.softDeleteAuthenticatedUserWalletById(wallet.getId()));
    }

    @Test
    void softDeleteAuthenticatedUserWalletById_AlreadyDeleted_Throws() {
        wallet.markAsDeleted();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        assertThrows(DuplicateEntityException.class, () -> walletService.softDeleteAuthenticatedUserWalletById(wallet.getId()));
    }

    @Test
    void softDeleteAuthenticatedUserWalletById_HasBalance_Throws() {
        wallet.setBalance(new BigDecimal("100.00"));
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        assertThrows(InvalidUserInputException.class, () -> walletService.softDeleteAuthenticatedUserWalletById(wallet.getId()));
    }

    @Test
    void createAuthenticatedUserWalletWalletByCurrency_Success() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.checkIfUserHasActiveWalletWithCurrency(user.getId(), Currency.USD)).thenReturn(false);
        when(walletRepository.findByUsernameAndCurrency(user.getUsername(), Currency.USD)).thenReturn(Optional.empty());
        Wallet created = walletService.createAuthenticatedUserWalletWalletByCurrency("USD");
        assertEquals(Currency.USD, created.getCurrency());
    }

    @Test
    void createAuthenticatedUserWalletWalletByCurrency_Duplicate_Throws() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.checkIfUserHasActiveWalletWithCurrency(user.getId(), Currency.USD)).thenReturn(true);
        assertThrows(DuplicateEntityException.class, () -> walletService.createAuthenticatedUserWalletWalletByCurrency(USD));
    }

    @Test
    void checkIfUserHasActiveWalletWithCurrency_ReturnsTrue() {
        when(walletRepository.checkIfUserHasActiveWalletWithCurrency(user.getId(), Currency.USD)).thenReturn(true);
        assertTrue(walletService.checkIfUserHasActiveWalletWithCurrency(user.getId(), Currency.USD));
    }

    @Test
    void update_SavesWallet() {
        walletService.update(wallet);
        verify(walletRepository).save(wallet);
    }

    @Test
    void softDeleteAuthenticatedUserWalletById_WalletNotFound_ThrowsEntityNotFound() {
        UUID walletId = UUID.randomUUID();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                walletService.softDeleteAuthenticatedUserWalletById(walletId));
    }

    @Test
    void getActiveWalletsOfAuthenticatedUser_NoWallets_EstimatedBalanceIsZero() {
        when(userService.getAuthenticatedUser()).thenReturn(user);

        when(walletRepository.findActiveWalletsByUserId(user.getId()))
                .thenReturn(List.of());

        List<Object[]> historyData = new ArrayList<>();
        createActivityRows(historyData, "30.00");

        when(walletRepository.findUserWalletHistory(eq(user.getUsername()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(historyData));

        WalletsWithHistoryOutput result = walletService.getActiveWalletsOfAuthenticatedUser(USD, 0, 10);

        assertEquals(BigDecimal.ZERO, result.getEstimatedBalance());
        assertEquals(USD, result.getEstimatedCurrency());
        assertTrue(result.getWallets().isEmpty());
        assertEquals(1, result.getHistory().size());
    }

    @Test
    void getOrCreateWalletByUsernameAndCurrency_ExistingWalletNotDeleted_ReturnsExisting() {
        Wallet existingWallet = createMockWallet(user);
        existingWallet.setDeleted(false);

        when(walletRepository.findByUsernameAndCurrency(user.getUsername(), existingWallet.getCurrency()))
                .thenReturn(Optional.of(existingWallet));

        Wallet result = walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(), existingWallet.getCurrency());

        assertEquals(existingWallet, result);
        verify(walletRepository, never()).save(any());
    }

    @Test
    void getActiveWalletsOfAuthenticatedUser_MatchingCurrency_ReturnsCorrectOutput() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        wallet.setCurrency(Currency.USD);

        when(walletRepository.findActiveWalletsByUserId(user.getId())).thenReturn(List.of(wallet));

        List<Object[]> activityRows = new ArrayList<>();

        createActivityRows(activityRows, VALUE);

        when(walletRepository.findUserWalletHistory(eq(user.getUsername()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(activityRows));


        when(exchangeRateService.findCurrentBalanceByCurrency(eq(USD), anyList()))
                .thenReturn(BigDecimal.valueOf(150));

        WalletsWithHistoryOutput result = walletService.getActiveWalletsOfAuthenticatedUser(USD, 0, 5);

        assertEquals(USD, result.getEstimatedCurrency());
        assertEquals(BigDecimal.valueOf(150), result.getEstimatedBalance());
        assertFalse(result.getWallets().isEmpty());
        assertFalse(result.getHistory().isEmpty());
    }

    @Test
    void getActiveWalletsOfAuthenticatedUser_NoMatchingCurrency_UsesFallback() {
        wallet.setCurrency(Currency.EUR);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.findActiveWalletsByUserId(user.getId())).thenReturn(List.of(wallet));
        when(walletRepository.findUserWalletHistory(eq(user.getUsername()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(exchangeRateService.findCurrentBalanceByCurrency(eq(USD), anyList()))
                .thenReturn(BigDecimal.valueOf(99));

        WalletsWithHistoryOutput result = walletService.getActiveWalletsOfAuthenticatedUser(USD, 0, 5);

        assertEquals("EUR", result.getEstimatedCurrency());
        assertEquals(BigDecimal.valueOf(99), result.getEstimatedBalance());
    }

    @Test
    void getActiveWalletsOfAuthenticatedUser_WithUnionActivityResult_WorksCorrectly() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        wallet.setCurrency(Currency.USD);

        when(walletRepository.findActiveWalletsByUserId(user.getId())).thenReturn(List.of(wallet));

        List<Object[]> activityRows = new ArrayList<>();
        createActivityRows(activityRows, VALUE);

        Page<Object[]> mockPage = new PageImpl<>(activityRows);
        when(walletRepository.findUserWalletHistory(eq(user.getUsername()), any(Pageable.class)))
                .thenReturn(mockPage);

        when(exchangeRateService.findCurrentBalanceByCurrency(eq(USD), anyList())).thenReturn(BigDecimal.valueOf(100));

        WalletsWithHistoryOutput result = walletService.getActiveWalletsOfAuthenticatedUser(USD, 0, 10);

        assertEquals(USD, result.getEstimatedCurrency());
        assertEquals(BigDecimal.valueOf(100), result.getEstimatedBalance());
        assertEquals(1, result.getHistory().size());
        assertEquals(1, result.getWallets().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getCurrentPage());
    }

    @Test
    void getWalletPageById_ValidRequest_ReturnsPageOutput() {
        UUID walletId = wallet.getId();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.checkIfUserHasActiveWalletWithId(user.getId(), walletId)).thenReturn(true);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        List<Object[]> activityRows = new ArrayList<>();
        createActivityRows(activityRows, VALUE);

        Page<Object[]> mockPage = new PageImpl<>(activityRows);
        when(walletRepository.findWalletHistory(eq(walletId), any(Pageable.class))).thenReturn(mockPage);
        when(walletRepository.findActiveWalletsByUserId(user.getId())).thenReturn(List.of(wallet));

        WalletPageOutput result = walletService.getWalletPageById(walletId, 0, 5);

        assertEquals(walletId, result.getWalletId());
        assertEquals(wallet.getBalance(), result.getBalance());
        assertEquals(wallet.getCurrency(), result.getCurrency());
        assertEquals(1, result.getHistory().size());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getWalletPageById_WalletNotOwnedByUser_ThrowsEntityNotFound() {
        UUID walletId = UUID.randomUUID();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.checkIfUserHasActiveWalletWithId(user.getId(), walletId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                walletService.getWalletPageById(walletId, 0, 5));
    }

    @Test
    void getWalletPageById_WalletNotFound_ThrowsEntityNotFound() {
        UUID walletId = UUID.randomUUID();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(walletRepository.checkIfUserHasActiveWalletWithId(user.getId(), walletId)).thenReturn(true);
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                walletService.getWalletPageById(walletId, 0, 5));
    }

    private static void createActivityRows(List<Object[]> activityRows, String val) {
        activityRows.add(new Object[]{
                UUID.randomUUID(), "transaction", new BigDecimal(val), null, USD,
                null, null, "alice", "bob", "/img/alice.png", "/img/bob.png", null, Timestamp.valueOf("2024-01-01 10:00:00")
        });
    }

}
