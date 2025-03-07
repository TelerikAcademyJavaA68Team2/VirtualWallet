package com.example.virtualwallet.models.dtos.wallet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityOutput {

    private UUID id;
    private String transactionType;
    private BigDecimal amount;
    private String currency;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal exchangeRate;
    private String senderUsername;
    private String recipientUsername;
    private String status;
    private LocalDateTime date;
}