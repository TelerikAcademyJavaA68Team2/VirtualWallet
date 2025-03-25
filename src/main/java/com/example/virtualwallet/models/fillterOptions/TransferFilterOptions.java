package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransferStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Data
public class TransferFilterOptions {

    private Optional<String> recipient;
    private Optional<LocalDateTime> fromDate;
    private Optional<LocalDateTime> toDate;
    private Optional<BigDecimal> minAmount;
    private Optional<BigDecimal> maxAmount;
    private Optional<Currency> currency;
    private Optional<TransferStatus> status;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;

    public TransferFilterOptions(String recipient,
                                 String fromDate,
                                 String toDate,
                                 BigDecimal minAmount,
                                 BigDecimal maxAmount,
                                 String currency,
                                 String status,
                                 String sortBy,
                                 String sortOrder,
                                 int page,
                                 int size) {
        this.recipient = sanitizeOptional(recipient);
        this.fromDate = parseLocalDateTime(fromDate);
        this.toDate = parseLocalDateTime(toDate);
        this.minAmount = sanitizeBigDecimal(minAmount);
        this.maxAmount = sanitizeBigDecimal(maxAmount);
        this.currency = sanitizeCurrency(currency);
        this.status = sanitizeTransferStatus(status);

        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;
    }

    private String validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("amount", "date", "status", "currency", "recipientUsername");
        return (sortBy != null && !sortBy.isEmpty() && validFields.contains(sortBy)) ? sortBy : "date";
    }

}