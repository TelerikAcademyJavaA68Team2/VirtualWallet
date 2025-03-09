package com.example.virtualwallet.models.dtos.exchangeRates;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRateOutput {

    private String fromCurrency;

    private String toCurrency;

    private BigDecimal rate;
}
