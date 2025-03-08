package com.example.virtualwallet.models.fillterOptions;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class ActivityFilterOptions {
    private Optional<LocalDateTime> startDate;
    private Optional<LocalDateTime> endDate;
    private Optional<String> transactionType;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;
    private int page;
    private int size;

    public ActivityFilterOptions(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String transactionType,
            String sortBy,
            String sortOrder,
            int page,
            int size
    ) {
        this.startDate = Optional.ofNullable(startDate);
        this.endDate = Optional.ofNullable(endDate);
        this.transactionType = Optional.ofNullable(transactionType);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
        this.page = page;
        this.size = size;
    }
}