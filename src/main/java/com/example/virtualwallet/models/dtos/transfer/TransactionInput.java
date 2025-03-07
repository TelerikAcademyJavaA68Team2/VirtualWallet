package com.example.virtualwallet.models.dtos.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionInput {

    @NotBlank(message = "User credentials cannot be blank!")
    private String usernameOrEmailOrPhoneNumber;

    @NotNull(message = "Amount cannot be null!")
    @Positive(message = "Amount must be positive number!")
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be blank or empty!")
    private String currency;

}
