package com.example.virtualwallet.models.dtos.pageable;

import com.example.virtualwallet.models.dtos.ActivityOutput;
import com.example.virtualwallet.models.enums.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class WalletPageOutput {

    private UUID walletId;

    private BigDecimal balance;

    private Currency currency;

    private int numberOfPages;

    private int totalResults;

    private List<ActivityOutput> activities;
}
