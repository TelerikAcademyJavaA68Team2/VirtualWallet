package com.example.virtualwallet.models.dtos.transfer;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FullTransferInfoOutput {

    private UUID transferId;

    private String recipientUsername;

    private BigDecimal amount;

    private String currency;

    private String status;

    private String cardNumbers;

    private LocalDateTime date;
}