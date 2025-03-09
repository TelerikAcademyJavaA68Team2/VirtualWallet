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
public class ExchangeFilterOptions {

    private Optional<LocalDateTime> fromDate;
    private Optional<LocalDateTime> toDate;
    private Optional<Currency> fromCurrency;
    private Optional<Currency> toCurrency;
    private Optional<BigDecimal> minStartAmount;
    private Optional<BigDecimal> maxStartAmount;
    private Optional<BigDecimal> minEndAmount;
    private Optional<BigDecimal> maxEndAmount;
    private Optional<String> recipientUsername;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;

    public ExchangeFilterOptions(String fromDate,
                                 String toDate,
                                 String fromCurrency,
                                 String toCurrency,
                                 BigDecimal minStartAmount,
                                 BigDecimal maxStartAmount,
                                 BigDecimal minEndAmount,
                                 BigDecimal maxEndAmount,
                                 String recipientUsername,
                                 String sortBy,
                                 String sortOrder,
                                 int page,
                                 int size) {
        this.fromDate = parseLocalDateTime(fromDate);
        this.toDate = parseLocalDateTime(toDate);
        this.fromCurrency = sanitizeCurrency(fromCurrency);
        this.toCurrency = sanitizeCurrency(toCurrency);
        this.minStartAmount = sanitizeBigDecimal(minStartAmount);
        this.maxStartAmount = sanitizeBigDecimal(maxStartAmount);
        this.minEndAmount = sanitizeBigDecimal(minEndAmount);
        this.maxEndAmount = sanitizeBigDecimal(maxEndAmount);
        this.recipientUsername = sanitizeOptional(recipientUsername);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;
    }

    private String validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("recipientUsername", "toAmount","amount", "fromCurrency", "toCurrency", "date");
        return (sortBy != null && !sortBy.isEmpty() && validFields.contains(sortBy)) ? sortBy : "date";
    }
}
