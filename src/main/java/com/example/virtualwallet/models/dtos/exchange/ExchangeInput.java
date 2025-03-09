package com.example.virtualwallet.models.dtos.exchange;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeInput {

    @NotBlank(message = "From currency cant be blank!")
    private String fromCurrency;

    @NotBlank(message = "To currency cant be blank!")
    private String toCurrency;

    @Positive(message = "The amount must be positive!")
    private BigDecimal amount;
}
