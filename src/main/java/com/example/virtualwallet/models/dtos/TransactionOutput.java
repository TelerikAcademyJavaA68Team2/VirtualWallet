package com.example.virtualwallet.models.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionOutput {

    private UUID transactionId;

    private LocalDateTime date;

    private BigDecimal amount;

    private String currency;

    private String senderUsername;

    private String recipientUsername;

}
