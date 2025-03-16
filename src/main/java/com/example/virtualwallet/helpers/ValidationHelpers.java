package com.example.virtualwallet.helpers;

import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.TransactionStatus;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Set;

public class ValidationHelpers {

    public static final String UNAUTHORIZED_MESSAGE_CARD = "Only the card's owner can modify card's details!";
    public static final String CARD_MUST_NOT_BE_EXPIRED = "Card must not be expired!";

    public static final Set<String> VALID_CURRENCIES_SET = Set.of("BGN", "USD", "EUR", "GBP", "JPY", "CNH", "AUD", "CAD", "CHF");
    public static final Set<String> VALID_TRANSACTION_STATUS_SET = Set.of("APPROVED", "DECLINED");
    public static final Set<String> VALID_ACCOUNT_STATUS_SET = Set.of("ACTIVE", "BLOCKED", "PENDING", "DELETED", "DELETED_AND_BLOCKED");
    public static final Set<String> VALID_ROLE_SET = Set.of("ADMIN", "USER");

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

    public static final DateTimeFormatter CUSTOM_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

    public static boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg")
                || contentType.equals("image/png") || contentType.equals("image/gif"));
    }

    public static void validateUserIsCardOwner(Card card, User user) {
        if (!card.getOwner().equals(user)) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_MESSAGE_CARD);
        }
    }

    public static void validateUserIsCardOwner(Card card, User user, String message) {
        if (!card.getOwner().equals(user)) {
            throw new UnauthorizedAccessException(message);
        }
    }

    public static void validateCardIsNotExpired(YearMonth expirationDate) {
        if (expirationDate.isBefore(YearMonth.now())) {
            throw new InvalidUserInputException(CARD_MUST_NOT_BE_EXPIRED);
        }
    }

    public static void validateCardIsNotExpired(YearMonth expirationDate, String message) {
        if (expirationDate.isBefore(YearMonth.now())) {
            throw new InvalidUserInputException(message);
        }
    }

    public static Currency validateAndConvertCurrency(String currencyString) {
        try {
            return Currency.valueOf(currencyString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidUserInputException("Currency: " + currencyString + " is not supported!");
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

    public static String convertToCustomFormat(String inputDate) {
        if (inputDate == null || inputDate.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(inputDate.trim(), INPUT_FORMATTER);
            return dateTime.format(OUTPUT_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format: " + inputDate +
                    ". Expected format: yyyy-MM-dd - HH:mm", ex);
        }
    }

    public static Optional<String> sanitizeOptional(String value) {
        return (value == null || value.trim().isEmpty()) ? Optional.empty() : Optional.of(value);
    }

    public static Optional<TransactionStatus> sanitizeTransactionStatus(String value) {
        return (value == null || value.trim().isEmpty() || !VALID_TRANSACTION_STATUS_SET.contains(value.toUpperCase()))
                ? Optional.empty() : Optional.of(TransactionStatus.valueOf(value.toUpperCase()));
    }

    public static Optional<Currency> sanitizeCurrency(String value) {
        return (value == null || value.trim().isEmpty() || !VALID_CURRENCIES_SET.contains(value.toUpperCase()))
                ? Optional.empty() : Optional.of(Currency.valueOf(value.toUpperCase()));
    }

    public static Optional<Role> sanitizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return Optional.empty();
        }
        return VALID_ROLE_SET.contains(role) ? Optional.of(Role.valueOf(role.toUpperCase())) : Optional.empty();
    }

    public static Optional<AccountStatus> sanitizeAccountStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return Optional.empty();
        }
        return VALID_ACCOUNT_STATUS_SET.contains(status) ? Optional.of(AccountStatus.valueOf(status.toUpperCase())) : Optional.empty();
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

    public static boolean requestIsWithInvalidPageOrSize(int page, int size) {
        return page < 0 || size <= 0;
    }
}