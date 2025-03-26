package com.example.virtualwallet.services;

import com.example.virtualwallet.Helpers;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientFundsException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.Exchange;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangePage;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import com.example.virtualwallet.repositories.ExchangeRepository;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.Helpers.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExchangeServiceImplTests {


    public static final String EUR = "EUR";
    public static final String INVALID_CURRENCY = "Invalid currency";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String WALLET = "Wallet";
    public static final String EXCHANGE = "Exchange";
    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    @Mock
    private User mockUser;

    @Mock
    private Wallet fromWallet;

    @Mock
    private Wallet toWallet;

    @BeforeEach
    void setUp() {
        exchangeRepository = mock(ExchangeRepository.class);
        exchangeRateService = mock(ExchangeRateService.class);
        walletService = mock(WalletService.class);
        userService = mock(UserService.class);

        exchangeService = new ExchangeServiceImpl(exchangeRepository, exchangeRateService, walletService, userService);

        mockUser = Helpers.createMockUserWithoutCardsAndWallets();
        fromWallet = Helpers.createMockWallet(mockUser);
        toWallet = Helpers.createMockWallet(mockUser);
        toWallet.setCurrency(Currency.EUR);
    }

    @Test
    void createExchange_ShouldSucceed_WhenValidInput() {

        ExchangeInput input = new ExchangeInput();
        input.setAmount(BigDecimal.valueOf(50));
        input.setFromCurrency(USD);
        input.setToCurrency(EUR);

        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(walletService.checkIfUserHasActiveWalletWithCurrency(mockUser.getId(), Currency.USD)).thenReturn(true);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(mockUser.getUsername(), Currency.USD)).thenReturn(fromWallet);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(mockUser.getUsername(), Currency.EUR)).thenReturn(toWallet);
        when(exchangeRateService.getRate(Currency.USD, Currency.EUR)).thenReturn(BigDecimal.valueOf(2));

        exchangeService.createExchange(input);

        verify(walletService).update(fromWallet);
        verify(walletService).update(toWallet);
        verify(exchangeRepository).save(any(Exchange.class));

        assertThat(fromWallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(toWallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
    }

    @Test
    void createExchange_ShouldThrow_WhenSameCurrency() {
        ExchangeInput input = new ExchangeInput();
        input.setAmount(BigDecimal.valueOf(50));
        input.setFromCurrency(USD);
        input.setToCurrency(USD);

        assertThatThrownBy(() -> exchangeService.createExchange(input))
                .isInstanceOf(InvalidUserInputException.class)
                .hasMessageContaining(INVALID_CURRENCY);
    }

    @Test
    void createExchange_ShouldThrow_WhenInsufficientFunds() {
        ExchangeInput input = new ExchangeInput();
        input.setAmount(BigDecimal.valueOf(150));
        input.setFromCurrency(USD);
        input.setToCurrency(EUR);

        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(walletService.checkIfUserHasActiveWalletWithCurrency(mockUser.getId(), Currency.USD)).thenReturn(true);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(mockUser.getUsername(), Currency.USD)).thenReturn(fromWallet);
        when(walletService.getOrCreateWalletByUsernameAndCurrency(mockUser.getUsername(), Currency.EUR)).thenReturn(toWallet);
        when(exchangeRateService.getRate(Currency.USD, Currency.EUR)).thenReturn(BigDecimal.ONE);

        assertThatThrownBy(() -> exchangeService.createExchange(input))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining(INSUFFICIENT_BALANCE);
    }

    @Test
    void createExchange_ShouldThrow_WhenFromWalletNotFound() {
        ExchangeInput input = new ExchangeInput();
        input.setAmount(BigDecimal.valueOf(10));
        input.setFromCurrency(USD);
        input.setToCurrency(EUR);

        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(walletService.checkIfUserHasActiveWalletWithCurrency(mockUser.getId(), Currency.USD)).thenReturn(false);

        assertThatThrownBy(() -> exchangeService.createExchange(input))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(WALLET);
    }

    @Test
    void getExchangeById_ShouldReturnExchange_WhenUserIsAdmin() {
        UUID exchangeId = UUID.randomUUID();
        User admin = Helpers.createMockAdmin();
        Exchange exchange = Helpers.createMockExchange(admin);
        exchange.setId(exchangeId);

        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(exchangeRepository.findById(exchangeId)).thenReturn(Optional.of(exchange));

        FullExchangeInfoOutput result = exchangeService.getExchangeById(exchangeId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(exchangeId);
    }


    @Test
    void getExchangeById_ShouldReturnExchange_WhenUserIsRecipient() {
        UUID exchangeId = UUID.randomUUID();
        Exchange exchange = createMockExchange(mockUser);
        exchange.setId(exchangeId);
        exchange.setRecipientUsername(mockUser.getUsername());

        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(exchangeRepository.findByIdAndRecipientUsername(exchangeId, mockUser.getUsername()))
                .thenReturn(Optional.of(exchange));

        FullExchangeInfoOutput result = exchangeService.getExchangeById(exchangeId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(exchangeId);
    }

    @Test
    void getExchangeById_ShouldThrow_WhenExchangeNotFound() {
        UUID exchangeId = UUID.randomUUID();

        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(exchangeRepository.findByIdAndRecipientUsername(exchangeId, mockUser.getUsername()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> exchangeService.getExchangeById(exchangeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(EXCHANGE);
    }

    @Test
    void filterExchanges_ShouldReturnCorrectPageData() {
        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions();
        filterOptions.setPage(0);
        filterOptions.setSize(2);
        filterOptions.setSortBy(Helpers.DATE);
        filterOptions.setSortOrder(SORT_ORDER);

        User user = Helpers.createMockUserWithoutCardsAndWallets();
        Exchange exchange1 = Helpers.createMockExchange(user);
        Exchange exchange2 = Helpers.createMockExchange(user);

        List<Exchange> exchangeList = List.of(exchange1, exchange2);
        Page<Exchange> mockPage = new PageImpl<>(exchangeList, PageRequest.of(0, 2), 4);

        when(exchangeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        ExchangePage result = exchangeService.filterExchanges(filterOptions);

        assertThat(result).isNotNull();
        assertThat(result.getExchanges()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getCurrentPage()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isHasNextPage()).isTrue();
        assertThat(result.isHasPreviousPage()).isFalse();
    }



}
