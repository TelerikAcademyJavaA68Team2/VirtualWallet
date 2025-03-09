package com.example.virtualwallet.models.fillterOptions;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Data
public class TransferFilterOptions {

    private Optional<UUID> userId;
    private Optional<LocalDateTime> minCreatedAt;
    private Optional<LocalDateTime> maxCreatedAt;
    private Optional<String> currency;
    private Optional<String> status;
    private Optional<String> cardNumber;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;

    public TransferFilterOptions(String firstDate,
                                 String lastDate,
                                 String currency,
                                 String status,
                                 String cardNumber,
                                 String sortBy,
                                 String sortOrder,
                                 int page,
                                 int size) {
        // For an admin scenario, we do not set userId
        this.userId = Optional.empty();

        this.minCreatedAt = parseLocalDateTime(firstDate);
        this.maxCreatedAt = parseLocalDateTime(lastDate);
        this.currency = sanitizeOptional(currency);
        this.status = sanitizeOptional(status);
        this.cardNumber = sanitizeOptional(cardNumber);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;
    }

    // A helper constructor that *does* set userId
    public TransferFilterOptions(UUID userId,
                                 String firstDate,
                                 String lastDate,
                                 String currency,
                                 String status,
                                 String cardNumber,
                                 String sortBy,
                                 String sortOrder,
                                 int page,
                                 int size) {
        this.userId = Optional.of(userId);

        this.minCreatedAt = parseLocalDateTime(firstDate);
        this.maxCreatedAt = parseLocalDateTime(lastDate);
        this.currency = sanitizeOptional(currency);
        this.status = sanitizeOptional(status);
        this.cardNumber = sanitizeOptional(cardNumber);
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
