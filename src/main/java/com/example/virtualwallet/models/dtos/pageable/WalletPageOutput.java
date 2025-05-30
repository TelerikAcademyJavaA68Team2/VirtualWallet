package com.example.virtualwallet.models.dtos.pageable;

import com.example.virtualwallet.models.dtos.wallet.ActivityOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class WalletPageOutput {

    private UUID walletId;

    private BigDecimal balance;

    private Currency currency;

    private List<ActivityOutput> history;

    private List<WalletBasicOutput> wallets;

    // Pagination metadata
    private long totalElements;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
}
