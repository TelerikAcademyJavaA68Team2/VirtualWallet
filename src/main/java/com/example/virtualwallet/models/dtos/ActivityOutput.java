package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;
import com.example.virtualwallet.models.enums.TransactionType;
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
    private Currency currency;
    private Currency fromCurrency;
    private Currency toCurrency;
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
        this.currency = (currency != null) ? Currency.valueOf(currency) : null;
        this.fromCurrency = (fromCurrency != null) ? Currency.valueOf(fromCurrency) : null;
        this.toCurrency = (toCurrency != null) ? Currency.valueOf(toCurrency) : null;
        this.exchangeRate = exchangeRate;
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.status = status;
        this.date = date;
    }
}