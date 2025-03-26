package com.example.virtualwallet.models.dtos.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletBasicOutput {

    private UUID walletId;

    private BigDecimal balance;

    private String currency;
}