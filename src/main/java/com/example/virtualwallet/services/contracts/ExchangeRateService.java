package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.ExchangeRate;
import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateOutput;
import com.example.virtualwallet.models.enums.Currency;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateService {


    void updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate);

    ExchangeRate getExchangeRate(String fromCurrency, String toCurrency);

    BigDecimal getTheRateOfExchangeRate(Currency fromCurrency, Currency toCurrency);

    List<ExchangeRateOutput> getAllExchangeRates();
}
