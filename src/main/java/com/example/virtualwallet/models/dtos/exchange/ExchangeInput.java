package com.example.virtualwallet.models.dtos.exchange;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;

@Data
@PropertySource("classpath:messages.properties")
public class ExchangeInput {

    @NotBlank(message = "{error.fromCurrencyBlank}")
    private String fromCurrency;

    @NotBlank(message = "{error.toCurrencyBlank}")
    private String toCurrency;

    @Positive(message = "{error.positiveNumber}")
    private BigDecimal amount;
}
