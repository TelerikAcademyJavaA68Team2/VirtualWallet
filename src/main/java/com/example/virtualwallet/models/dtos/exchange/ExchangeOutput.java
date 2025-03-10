package com.example.virtualwallet.models.dtos.exchange;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExchangeOutput {

    private UUID id;

    private String recipientUsername;

    private BigDecimal amount;

    private BigDecimal toAmount;

    private String fromCurrency;

    private String toCurrency;

    private LocalDateTime date;
}
