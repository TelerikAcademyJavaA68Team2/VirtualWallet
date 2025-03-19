package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.models.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Data
public class TransactionFilterOptions {

    private Optional<String> profileUsername;
    private Optional<LocalDateTime> fromDate;
    private Optional<LocalDateTime> toDate;
    private Optional<BigDecimal> minAmount;
    private Optional<BigDecimal> maxAmount;
    private Optional<Currency> currency;
    private Optional<String> sender;
    private Optional<String> recipient;
    private Optional<String> direction;
    private Optional<String> description;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;

    public TransactionFilterOptions(
            String profileUsername,
            String fromDate,
            String toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String currency,
            String sender,
            String recipient,
            String direction,
            String description,
            String sortBy,
            String sortOrder,
            int page,
            int size
    ) {
        this.profileUsername = sanitizeOptional(profileUsername);
        this.fromDate = parseLocalDateTime(fromDate);
        this.toDate = parseLocalDateTime(toDate);
        this.minAmount = sanitizeBigDecimal(minAmount);
        this.maxAmount = sanitizeBigDecimal(maxAmount);
        this.currency = sanitizeCurrency(currency);
        this.sender = sanitizeOptional(sender);
        this.recipient = sanitizeOptional(recipient);
        this.direction = sanitizeOptional(direction);
        this.description = sanitizeOptional(description);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;
    }

    private String validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("amount", "date", "currency", "senderUsername", "recipientUsername","description");
        return (sortBy != null && !sortBy.isEmpty() && validFields.contains(sortBy)) ? sortBy : "date";
    }
}