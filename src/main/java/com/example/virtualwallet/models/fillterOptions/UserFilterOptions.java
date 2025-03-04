package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class UserFilterOptions {

    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<String> role;
    private Optional<String> accountStatus;
    private Optional<BigDecimal> minTotalBalance;
    private Optional<BigDecimal> maxTotalBalance;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public UserFilterOptions(String username,
                             String email,
                             String phoneNumber,
                             String role,
                             String accountStatus,
                             BigDecimal minTotalBalance,
                             BigDecimal maxTotalBalance,
                             String sortBy,
                             String sortOrder
    ) {
        this.username = sanitizeOptional(username);
        this.email = sanitizeOptional(email);
        this.phoneNumber = sanitizeOptional(phoneNumber);
        this.role = validateRole(role);
        this.accountStatus = validateAccountStatus(accountStatus);
        this.minTotalBalance = Optional.ofNullable(minTotalBalance);
        this.maxTotalBalance = Optional.ofNullable(maxTotalBalance);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortBy,sortOrder);
    }

    private Optional<String> sanitizeOptional(String value) {
        return (value == null || value.trim().isEmpty()) ? Optional.empty() : Optional.of(value);
    }

    private Optional<String> validateRole(String role) {
        List<String> validRoles = Arrays.stream(Role.values())
                .map(Enum::name)
                .toList();
        return validRoles.contains(role) ? Optional.of(role) : Optional.empty();
    }

    private Optional<String> validateAccountStatus(String accountStatus) {
        List<String> validAccountStatuses = Arrays.stream(AccountStatus.values())
                .map(Enum::name)
                .toList();
        return validAccountStatuses.contains(accountStatus) ? Optional.of(accountStatus) : Optional.empty();
    }

    private Optional<String> validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("username", "email", "phoneNumber", "role", "status", "totalBalance");
        return (sortBy == null || sortBy.isEmpty() || validFields.contains(sortBy))
                ? Optional.ofNullable(sortBy)
                : Optional.of("username");
    }

    private Optional<String> validateSortOrder(String sortBy, String sortOrder) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Optional.empty();
        }
        return ("asc".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder))
                ? Optional.of(sortOrder.toLowerCase())
                : Optional.of("asc");
    }
}