package com.example.virtualwallet.models.dtos.wallet;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityOutput {

    private UUID id;
    private String activity;
    private BigDecimal amount;
    private BigDecimal toAmount;
    private String currency;
    private String fromCurrency;
    private String toCurrency;
    private String senderUsername;
    private String recipientUsername;
    private String senderPhoto;
    private String recipientPhoto;
    private String status;
    private LocalDateTime date;

    public ActivityOutput(
            UUID id,
            String activity,
            BigDecimal amount,
            BigDecimal toAmount,
            String currency,
            String fromCurrency,
            String toCurrency,
            String senderUsername,
            String recipientUsername,
            String senderPhoto,
            String recipientPhoto,
            String status,
            LocalDateTime date
    ) {
        this.id = id;
        this.activity = activity;
        this.amount = amount;
        this.toAmount = toAmount;
        this.currency = currency;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.senderPhoto = senderPhoto;
        this.recipientPhoto = recipientPhoto;
        this.status = status;
        this.date = date;
    }
}