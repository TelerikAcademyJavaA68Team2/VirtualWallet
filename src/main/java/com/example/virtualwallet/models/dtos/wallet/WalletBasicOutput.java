package com.example.virtualwallet.models.dtos.wallet;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletBasicOutput {

    private UUID walletId;

    private BigDecimal balance;

    private String currency;
}