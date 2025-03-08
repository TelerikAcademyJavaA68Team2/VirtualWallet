package com.example.virtualwallet.models.fillterOptions;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Data
public class TransferFilterOptions {

    private Optional<LocalDateTime> minCreatedAt;
    private Optional<LocalDateTime> maxCreatedAt;
    private Optional<String> currency;
    private Optional<String> status;
    private Optional<String> cardId;
    private Optional<String> walletId;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;


    public TransferFilterOptions(String firstDate,
                                    String lastDate,
                                    String currency,
                                    String status,
                                    String cardId,
                                    String walletId,
                                    String sortBy,
                                    String sortOrder,
                                    int page,
                                    int size

    ) {
        this.minCreatedAt = parseLocalDateTime(firstDate);
        this.maxCreatedAt = parseLocalDateTime(lastDate);
        this.currency = sanitizeOptional(currency);
        this.status = sanitizeOptional(status);
        this.cardId = sanitizeOptional(cardId);
        this.walletId = sanitizeOptional(walletId);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;

    }

    private String validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("amount", "date", "status");
        return (sortBy != null && !sortBy.isEmpty() && validFields.contains(sortBy)) ? sortBy : "date";
    }

}
