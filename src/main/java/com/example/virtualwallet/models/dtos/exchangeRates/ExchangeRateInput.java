package com.example.virtualwallet.models.dtos.exchangeRates;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRateInput {

    @NotBlank
    private String fromCurrency;

    @NotBlank
    private String toCurrency;

    @Positive
    private BigDecimal exchangeRate;

}