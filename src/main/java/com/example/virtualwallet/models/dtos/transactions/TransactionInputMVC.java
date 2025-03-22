package com.example.virtualwallet.models.dtos.transactions;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@PropertySource("classpath:messages.properties")
public class TransactionInputMVC {

    @NotNull
    private UUID recipientId;

    @NotNull
    private String walletId;

    @NotNull(message = "{error.transactionAmountBlank}")
    @Positive(message = "{error.transactionAmountPositive}")
    private BigDecimal amount;

    @Size(max = 50)
    private String description;
}
