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

import static com.example.virtualwallet.helpers.ValidationHelpers.VALID_CURRENCIES_SET;


@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
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
        if (VALID_CURRENCIES_SET.contains(fromCurrency.toUpperCase()) && VALID_CURRENCIES_SET.contains(toCurrency.toUpperCase())) {
           if (fromCurrency.equalsIgnoreCase(toCurrency)){
               throw new InvalidUserInputException("Invalid exchange request");
           }
            enumFromCurrency = Currency.valueOf(fromCurrency.toUpperCase());
            enumToCurrency = Currency.valueOf(toCurrency.toUpperCase());
        } else {
            throw new InvalidUserInputException("Invalid currency provided");
        }
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(enumFromCurrency, enumToCurrency);
    }

    @Override
    public BigDecimal getRate(Currency fromCurrency, Currency toCurrency) {
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency).getRate();
    }

    @Override
    public List<ExchangeRateOutput> getAllExchangeRates() {
        return exchangeRateRepository.findAll().stream().map(ModelMapper::exchangeRateOutputFromExchangeRate).toList();
    }
}