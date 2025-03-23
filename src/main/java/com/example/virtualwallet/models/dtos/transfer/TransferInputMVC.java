package com.example.virtualwallet.models.dtos.transfer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;

@Data
@PropertySource("classpath:messages.properties")
public class TransferInputMVC {

    @NotNull(message = "{error.cardIdEmpty}")
    private String cardId;

    @NotNull(message = "{error.walletIdEmpty}")
    private String walletId;

    @NotNull(message = "{error.amountEmpty}")
    @Positive(message = "{error.amountPositive}")
    private BigDecimal amount;

}
