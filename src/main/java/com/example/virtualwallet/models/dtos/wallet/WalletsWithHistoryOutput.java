package com.example.virtualwallet.models.dtos.wallet;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletsWithHistoryOutput {

    private BigDecimal estimatedBalance;

    private List<WalletBasicOutput> wallets;

    private List<ActivityOutput> history;
}