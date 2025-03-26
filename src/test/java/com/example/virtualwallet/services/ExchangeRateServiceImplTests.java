package com.example.virtualwallet.services;

import com.example.virtualwallet.Helpers;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.ExchangeRate;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExchangeRateServiceImplTests {

    public static final String API_FAILURE = "Simulated API failure";
    public static final String TEST_PROFILE = "test";
    public static final String EUR = "EUR";
    public static final String EXPECTED_RATE = "1.2";
    public static final String INVALID_EXCHANGE_REQUEST = "Invalid exchange request";
    public static final String INVALID_CURRENCY = "Invalid currency";
    public static final String SUCCESS = "success";
    public static final String RESULT = "result";
    public static final String CONVERSION_RATES = "conversion_rates";
    public static final String ACTIVE_PROFILE = "prod";
    public static final String USD_2 = "usd";
    public static final String EXPECTED_AMOUNT = "200.00";
    public static final String FROM_CURRENCY = "XXX";
    public static final String EXPECTED = "1.5";
    public static final String POSITIVE = "positive";

    private ExchangeRateRepository exchangeRateRepository;
    private RestTemplate restTemplate;
    private Environment environment;
    private ExchangeRateServiceImpl service;

    @BeforeEach
    void setUp() {
        exchangeRateRepository = mock(ExchangeRateRepository.class);
        restTemplate = mock(RestTemplate.class);
        environment = mock(Environment.class);
        service = new ExchangeRateServiceImpl(environment, restTemplate, exchangeRateRepository);
    }

    @Test
    void updateExchangeRate_ShouldUpdate_WhenValid() {
        ExchangeRate rate = Helpers.createMockExchangeRate(Currency.USD, Currency.EUR, BigDecimal.ONE);
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(Currency.USD, Currency.EUR)).thenReturn(rate);

        service.updateExchangeRate(Helpers.USD, EUR, BigDecimal.valueOf(1.5));

        verify(exchangeRateRepository).save(rate);
        assertThat(rate.getRate()).isEqualByComparingTo(EXPECTED);
    }

    @Test
    void updateExchangeRate_ShouldThrow_WhenNegative() {
        ExchangeRate rate = Helpers.createMockExchangeRate(Currency.USD, Currency.EUR, BigDecimal.ONE);
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(Currency.USD, Currency.EUR)).thenReturn(rate);

        assertThatThrownBy(() ->
                service.updateExchangeRate(Helpers.USD, EUR, BigDecimal.valueOf(-5)))
                .isInstanceOf(InvalidUserInputException.class)
                .hasMessageContaining(POSITIVE);
    }

    @Test
    void findCurrentBalanceByCurrency_ShouldConvertAndSumCorrectly() {
        WalletBasicOutput walletUSD = new WalletBasicOutput(UUID.randomUUID(), BigDecimal.valueOf(100), Helpers.USD);
        WalletBasicOutput walletEUR = new WalletBasicOutput(UUID.randomUUID(), BigDecimal.valueOf(50), EUR);

        ExchangeRate eurToUsd = Helpers.createMockExchangeRate(Currency.EUR, Currency.USD, BigDecimal.valueOf(2));
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(Currency.EUR, Currency.USD)).thenReturn(eurToUsd);

        BigDecimal result = service.findCurrentBalanceByCurrency(Helpers.USD, List.of(walletUSD, walletEUR));

        assertThat(result).isEqualByComparingTo(EXPECTED_AMOUNT);
    }

    @Test
    void getExchangeRate_ShouldReturn_WhenValid() {
        ExchangeRate expected = Helpers.createMockExchangeRate(Currency.USD, Currency.EUR, BigDecimal.ONE);
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(Currency.USD, Currency.EUR)).thenReturn(expected);

        ExchangeRate actual = service.getExchangeRate(Helpers.USD, EUR);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getExchangeRate_ShouldThrow_WhenInvalidCurrency() {
        assertThatThrownBy(() -> service.getExchangeRate(FROM_CURRENCY, Helpers.USD))
                .isInstanceOf(InvalidUserInputException.class)
                .hasMessageContaining(INVALID_CURRENCY);
    }

    @Test
    void getExchangeRate_ShouldThrow_WhenSameCurrency() {
        assertThatThrownBy(() -> service.getExchangeRate(Helpers.USD, USD_2))
                .isInstanceOf(InvalidUserInputException.class)
                .hasMessageContaining(INVALID_EXCHANGE_REQUEST);
    }

    @Test
    void getRate_ShouldReturnRateValue() {
        ExchangeRate rate = Helpers.createMockExchangeRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(1.2));
        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(Currency.USD, Currency.EUR)).thenReturn(rate);

        BigDecimal actual = service.getRate(Currency.USD, Currency.EUR);
        assertThat(actual).isEqualByComparingTo(EXPECTED_RATE);
    }

    @Test
    void getAllExchangeRates_ShouldReturnListOfMappedOutputs() {
        ExchangeRate r1 = Helpers.createMockExchangeRate(Currency.USD, Currency.EUR, BigDecimal.ONE);
        ExchangeRate r2 = Helpers.createMockExchangeRate(Currency.EUR, Currency.USD, BigDecimal.ONE);
        when(exchangeRateRepository.findAll()).thenReturn(List.of(r1, r2));

        var result = service.getAllExchangeRates();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFromCurrency()).isEqualTo(Helpers.USD);
        assertThat(result.get(1).getFromCurrency()).isEqualTo(EUR);
    }

    @Test
    void fetchAndUpdateAllExchangeRatesDaily_ShouldUpdate_WhenApiResponds() {
        Currency from = Currency.USD;
        Currency to = Currency.EUR;

        Map<String, Double> conversionRates = new HashMap<>();
        conversionRates.put(to.name(), 1.5);

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put(RESULT, SUCCESS);
        mockResponse.put(CONVERSION_RATES, conversionRates);

        when(restTemplate.getForObject(contains("/" + from.name()), eq(Map.class)))
                .thenReturn(mockResponse);

        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(from, to)).thenReturn(null);

        service.fetchAndUpdateAllExchangeRatesDaily();

        verify(exchangeRateRepository, atLeastOnce()).save(argThat(rate ->
                rate.getFromCurrency() == from &&
                        rate.getToCurrency() == to &&
                        rate.getRate().compareTo(BigDecimal.valueOf(1.5)) == 0
        ));
    }

    @Test
    void initializeExchangeRatesAPIUpdate_ShouldNotRunInTestProfile() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{TEST_PROFILE});

        service.initializeExchangeRatesAPIUpdate();

        verifyNoInteractions(restTemplate);
    }

    @Test
    void initializeExchangeRatesAPIUpdate_ShouldRunInNonTestProfile() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{ACTIVE_PROFILE});

        Map<String, Object> response = new HashMap<>();
        response.put(RESULT, SUCCESS);
        response.put(CONVERSION_RATES, Map.of(EUR, 1.5));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        service.initializeExchangeRatesAPIUpdate();

        verify(restTemplate, atLeastOnce()).getForObject(anyString(), eq(Map.class));
    }

    @Test
    void fetchAndUpdateAllExchangeRatesDaily_ShouldCatchException_WhenRestCallFails() {
        Currency failingCurrency = Currency.USD;

        when(restTemplate.getForObject(contains("/" + failingCurrency.name()), eq(Map.class)))
                .thenThrow(new RuntimeException(API_FAILURE));

        service.fetchAndUpdateAllExchangeRatesDaily();

        verify(exchangeRateRepository, never()).save(any());

    }
}