package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.ExchangeRate;
import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.ExchangeRateRepository;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.example.virtualwallet.helpers.ValidationHelpers.VALID_CURRENCIES_SET;
import static com.example.virtualwallet.services.ExchangeServiceImpl.INVALID_CURRENCY;


@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    public static final String INVALID_EXCHANGE_REQUEST = "Invalid exchange request";
    public static final String RATE_MUST_BE_POSITIVE = "Exchange rate must be positive!";

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
            }else {
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
           if (fromCurrency.equalsIgnoreCase(toCurrency)){
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

    @Override
    public List<ExchangeRateOutput> getAllExchangeRates() {
        return exchangeRateRepository.findAll().stream().map(ModelMapper::exchangeRateOutputFromExchangeRate).toList();
    }
}