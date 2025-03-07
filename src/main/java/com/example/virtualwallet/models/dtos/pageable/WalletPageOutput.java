package com.example.virtualwallet.models.dtos.pageable;

import com.example.virtualwallet.models.dtos.wallet.ActivityOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
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

    private int historyPages;

    private int historySize;

    private List<ActivityOutput> activities;
}
