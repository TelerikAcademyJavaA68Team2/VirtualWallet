package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.ExchangeRate;
import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.ExchangeRateRepository;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final Set<String> validCurrencies = Set.of("BGN", "USD", "EUR");
    private final ExchangeRateRepository exchangeRateRepository;


    @Override
    public void updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate) {
        ExchangeRate exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidUserInputException("Exchange rate must be positive!");
        }
        exchangeRate.setRate(rate);
        exchangeRateRepository.save(exchangeRate);
    }

    @Override
    public ExchangeRate getExchangeRate(String fromCurrency, String toCurrency) {
        Currency enumFromCurrency;
        Currency enumToCurrency;
        if (validCurrencies.contains(fromCurrency.toUpperCase()) && validCurrencies.contains(toCurrency.toUpperCase())) {
            enumFromCurrency = Currency.valueOf(fromCurrency.toUpperCase());
            enumToCurrency = Currency.valueOf(toCurrency.toUpperCase());
        } else {
            throw new InvalidUserInputException("Invalid currency provided");
        }
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(enumFromCurrency, enumToCurrency);
    }

    @Override
    public BigDecimal getTheRateOfExchangeRate(Currency fromCurrency, Currency toCurrency) {
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency).getRate();
    }

    @Override
    public List<ExchangeRateOutput> getAllExchangeRates() {
        return exchangeRateRepository.findAll().stream().map(ModelMapper::exchangeRateOutputFromExchangeRate).toList();
    }
}