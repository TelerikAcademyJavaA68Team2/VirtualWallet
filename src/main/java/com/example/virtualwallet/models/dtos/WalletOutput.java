package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class WalletOutput {

    private UUID walletId;

    private BigDecimal balance;

    private Currency currency;

    private List<ActivityOutput> activities;
}
