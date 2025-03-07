package com.example.virtualwallet.models.dtos.wallet;

import com.example.virtualwallet.models.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletBasicOutput {

    private UUID walletId;

    private BigDecimal balance;

    private Currency currency;
}