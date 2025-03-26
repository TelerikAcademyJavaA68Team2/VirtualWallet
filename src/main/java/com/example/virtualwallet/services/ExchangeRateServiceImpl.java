package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.ExchangeRate;
import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import com.example.virtualwallet.repositories.ExchangeRateRepository;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.example.virtualwallet.helpers.ValidationHelpers.VALID_CURRENCIES_SET;
import static com.example.virtualwallet.services.ExchangeServiceImpl.INVALID_CURRENCY;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    public static final String INVALID_EXCHANGE_REQUEST = "Invalid exchange request";
    public static final String RATE_MUST_BE_POSITIVE = "Exchange rate must be positive!";
    public static final String FAILED_UPDATE = "Failed to update exchange rates from: ";
    public static final String UPDATED_AT = "Exchange rates updated at: ";

    @Value("${exchangerate.api.base-url}")
    private String baseUrl;

    @Value("${exchangerate.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final ExchangeRateRepository exchangeRateRepository;


    @Override
    public void updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate) {
        ExchangeRate exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidUserInputException(RATE_MUST_BE_POSITIVE);
        }
        exchangeRate.setRate(rate);
        exchangeRateRepository.save(exchangeRate);
    }

    @Override
    public BigDecimal findCurrentBalanceByCurrency(String mainCurrency, List<WalletBasicOutput> wallets) {
        BigDecimal sum = BigDecimal.ZERO;
        for (WalletBasicOutput wallet : wallets) {
            if (mainCurrency.equalsIgnoreCase(wallet.getCurrency())) {
                sum = sum.add(wallet.getBalance());
            } else {
                sum = sum.add(wallet.getBalance()
                        .multiply(getExchangeRate(wallet.getCurrency(), mainCurrency).getRate()));
            }
        }
        return sum.setScale(2, RoundingMode.HALF_UP);
    }


    @Override
    public ExchangeRate getExchangeRate(String fromCurrency, String toCurrency) {
        Currency enumFromCurrency;
        Currency enumToCurrency;
        if (VALID_CURRENCIES_SET.contains(fromCurrency.toUpperCase()) && VALID_CURRENCIES_SET.contains(toCurrency.toUpperCase())) {
            if (fromCurrency.equalsIgnoreCase(toCurrency)) {
                throw new InvalidUserInputException(INVALID_EXCHANGE_REQUEST);
            }
            enumFromCurrency = Currency.valueOf(fromCurrency.toUpperCase());
            enumToCurrency = Currency.valueOf(toCurrency.toUpperCase());
        } else {
            throw new InvalidUserInputException(INVALID_CURRENCY);
        }
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(enumFromCurrency, enumToCurrency);
    }

    @Override
    public BigDecimal getRate(Currency fromCurrency, Currency toCurrency) {
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency).getRate();
    }

    @Scheduled(cron = "2 1 0 * * *", zone = "UTC")
    public void fetchAndUpdateAllExchangeRatesDaily() {
        LocalDateTime timeOfUpdate = LocalDateTime.now();
        for (Currency fromCurrency : Currency.values()) {
            try {

                String url = String.format("%s/%s/latest/%s", baseUrl, apiKey, fromCurrency.name());
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                if (response != null && "success".equals(response.get("result"))) {
                    Map<String, Double> rates = (Map<String, Double>) response.get("conversion_rates");

                    for (Currency toCurrency : Currency.values()) {
                        if (!fromCurrency.equals(toCurrency) && rates != null && rates.containsKey(toCurrency.name())) {
                            BigDecimal rateValue = BigDecimal.valueOf(rates.get(toCurrency.name()));

                            ExchangeRate exchangeRate = exchangeRateRepository
                                    .findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);

                            if (exchangeRate == null) {
                                exchangeRate = new ExchangeRate();
                                exchangeRate.setFromCurrency(fromCurrency);
                                exchangeRate.setToCurrency(toCurrency);
                                exchangeRate.setLastUpdated(timeOfUpdate);
                            }

                            exchangeRate.setRate(rateValue);
                            exchangeRate.setLastUpdated(timeOfUpdate);
                            exchangeRateRepository.save(exchangeRate);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(FAILED_UPDATE + fromCurrency + "at" + timeOfUpdate);
                e.printStackTrace();
            }
        }
        System.out.println(UPDATED_AT + timeOfUpdate);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Profile("!test")
    public void initializeExchangeRatesOnStartup() {
        fetchAndUpdateAllExchangeRatesDaily();
    }

    @Override
    public List<ExchangeRateOutput> getAllExchangeRates() {
        return exchangeRateRepository.findAll().stream().map(ModelMapper::exchangeRateOutputFromExchangeRate).toList();
    }
}