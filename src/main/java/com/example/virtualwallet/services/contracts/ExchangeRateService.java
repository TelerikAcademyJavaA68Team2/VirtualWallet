package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.ExchangeRate;
import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateService {

    BigDecimal findCurrentBalanceByCurrency(String mainCurrency, List<WalletBasicOutput> wallets);

    void updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate);

    ExchangeRate getExchangeRate(String fromCurrency, String toCurrency);

    BigDecimal getRate(Currency fromCurrency, Currency toCurrency);

    List<ExchangeRateOutput> getAllExchangeRates();
}
