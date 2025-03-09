package com.example.virtualwallet.models.fillterOptions;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Data
public class TransactionFilterOptions {

    private Optional<LocalDateTime> minCreatedAt;
    private Optional<LocalDateTime> maxCreatedAt;
    private Optional<String> currency;
    private Optional<String> sender;
    private Optional<String> recipient;
    private Optional<String> direction;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;

    public TransactionFilterOptions(String firstDate,
                            String lastDate,
                            String currency,
                            String sender,
                            String recipient,
                            String direction,
                            String sortBy,
                            String sortOrder,
                                    int page,
                                    int size

    ) {
        this.minCreatedAt = parseLocalDateTime(firstDate);
        this.maxCreatedAt = parseLocalDateTime(lastDate);
        this.currency = sanitizeOptional(currency);
        this.sender = sanitizeOptional(sender);
        this.recipient = sanitizeOptional(recipient);
        this.direction = sanitizeOptional(direction);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;

    }

    private String validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("amount", "date");
        return (sortBy != null && !sortBy.isEmpty() && validFields.contains(sortBy)) ? sortBy : "date";
    }
}
