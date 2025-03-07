package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.exceptions.InvalidUserInputException;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
public class TransactionFilterOptions {

    private static final DateTimeFormatter CUSTOM_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

    private Optional<LocalDateTime> minCreatedAt;
    private Optional<LocalDateTime> maxCreatedAt;
    private Optional<String> sender;
    private Optional<String> recipient;
    private Optional<String> direction;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;

    public TransactionFilterOptions(String firstDate,
                            String lastDate,
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
        this.sender = sanitizeOptional(sender);
        this.recipient = sanitizeOptional(recipient);
        this.direction = sanitizeOptional(direction);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;

    }

    private Optional<LocalDateTime> parseLocalDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(value.trim(), CUSTOM_FORMATTER));
        } catch (DateTimeParseException ex) {
            // return Optional.empty();


            throw new InvalidUserInputException("Invalid date/time format: " + value +
                    ". Expected format: dd.MM.yyyy - HH:mm", ex);
        }
    }

    private Optional<String> sanitizeOptional(String value) {
        return (value == null || value.trim().isEmpty()) ? Optional.empty() : Optional.of(value);
    }

    private String validateSortOrder(String sortOrder) {
        return (sortOrder != null &&
                !sortOrder.isEmpty() &&
                (sortOrder.equals("asc") || sortOrder.equals("desc"))) ? sortOrder : "desc";
    }

    private String validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("amount", "date");
        return (sortBy != null && !sortBy.isEmpty() && validFields.contains(sortBy)) ? sortBy : "date";
    }
}
