package com.example.virtualwallet.models.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransferOutput {

    private UUID transferId;

    private LocalDateTime date;

    private String status;

    private BigDecimal amount;

    private String currency;

    private UUID cardId;

    private UUID walletId;

}
