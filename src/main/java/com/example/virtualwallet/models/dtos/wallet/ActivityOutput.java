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

    public ActivityOutput(UUID id,
                          String transactionType,
                          BigDecimal amount,
                          String currency,
                          String fromCurrency,
                          String toCurrency,
                          BigDecimal exchangeRate,
                          String senderUsername,
                          String recipientUsername,
                          String status,
                          LocalDateTime date) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currency = currency;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.status = status;
        this.date = date;
    }
}