package com.example.virtualwallet.models.dtos.transactions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@PropertySource("classpath:messages.properties")
public class TransactionInput {

    @NotBlank(message = "{error.transactionFindUserByBlank}")
    private String usernameOrEmailOrPhoneNumber;

    @NotNull(message = "{error.transactionAmountBlank}")
    @Positive(message = "{error.transactionAmountPositive}")
    private BigDecimal amount;

    @NotBlank(message = "{error.currencyEmpty}")
    private String currency;

    private String description;
}