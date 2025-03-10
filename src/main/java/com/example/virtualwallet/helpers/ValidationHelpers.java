package com.example.virtualwallet.helpers;

import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Currency;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Set;

public class ValidationHelpers {

    public static final String UNAUTHORIZED_MESSAGE_POST = "Only the card's owner can modify cards!";

    public static final Set<String> VALID_CURRENCIES_SET = Set.of("BGN", "USD", "EUR");

    public static final DateTimeFormatter CUSTOM_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

    public static boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg")
                || contentType.equals("image/png") || contentType.equals("image/gif"));
    }

    public static void validateUserIsCardOwner(Card card, User user) {
        if (!card.getOwner().equals(user)) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_MESSAGE_POST);
        }
    }

    public static Currency validateAndConvertCurrency(String currencyString) {
        try {
            return Currency.valueOf(currencyString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidUserInputException("Invalid currency: " + currencyString +
                    ". Supported currencies: BGN, EUR, USD.");
        }
    }

    public static Optional<LocalDateTime> parseLocalDateTime(String value) {
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

    public static Optional<String> sanitizeOptional(String value) {
        return (value == null || value.trim().isEmpty()) ? Optional.empty() : Optional.of(value);
    }

    public static Optional<Currency> sanitizeCurrency(String value) {
        return (value == null || value.trim().isEmpty() || !VALID_CURRENCIES_SET.contains(value.toUpperCase()))
                ? Optional.empty() : Optional.of(Currency.valueOf(value.toUpperCase()));
    }

    public static Optional<BigDecimal> sanitizeBigDecimal(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    public static String validateSortOrder(String sortOrder) {
        return (sortOrder != null &&
                !sortOrder.isEmpty() &&
                (sortOrder.equals("asc") || sortOrder.equals("desc"))) ? sortOrder : "desc";
    }

    public static boolean validPageAndSize(int page, int size) {
        return page < 0 || size <= 0;
    }
}
