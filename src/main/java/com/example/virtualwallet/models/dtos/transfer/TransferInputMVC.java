package com.example.virtualwallet.models.dtos.transfer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@PropertySource("classpath:messages.properties")
public class TransferInputMVC {

    @NotNull(message = "{error.cardIdEmpty}")
    private UUID cardId;

    @NotNull(message = "{error.walletIdEmpty}")
    private UUID walletId;

    @NotNull(message = "{error.amountEmpty}")
    @Positive(message = "{error.amountPositive}")
    private BigDecimal amount;

}
