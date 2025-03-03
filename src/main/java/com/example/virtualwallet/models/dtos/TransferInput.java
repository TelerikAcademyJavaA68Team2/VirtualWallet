package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferInput {

    @NotNull(message = "Card ID cannot be null!")
    private UUID cardId;

    @NotNull(message = "Amount cannot be null!")
    @Positive(message = "Amount must be positive number!")
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be null or empty!")
    private String currency;
}
