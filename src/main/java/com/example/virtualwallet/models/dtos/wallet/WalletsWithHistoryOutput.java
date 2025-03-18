package com.example.virtualwallet.models.dtos.wallet;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletsWithHistoryOutput {

    private BigDecimal estimatedBalance;
    private String estimatedCurrency;

    private List<WalletBasicOutput> wallets;

    private List<ActivityOutput> history;

    // Pagination metadata
    private long totalElements;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
}