package com.example.virtualwallet.models.dtos.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@PropertySource("classpath:messages.properties")
public class TransferInput {

    @NotNull(message = "{error.cardIdEmpty}")
    private UUID cardId;

    @NotNull(message = "{error.amountEmpty}")
    @Positive(message = "{error.amountPositive}")
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be empty!")
    private String currency;
}
