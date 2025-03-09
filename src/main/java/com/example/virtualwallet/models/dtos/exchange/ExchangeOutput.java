package com.example.virtualwallet.models.dtos.exchange;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeOutput {

    private String fromCurrency;

    private String toCurrency;

    private BigDecimal amount;
}
