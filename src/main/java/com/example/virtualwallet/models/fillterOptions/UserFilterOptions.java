package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public class UserFilterOptions {

    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<String> role;
    private Optional<String> status;
    private Optional<LocalDateTime> minCreatedAt;
    private Optional<LocalDateTime> maxCreatedAt;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;


    public UserFilterOptions(String username,
                             String email,
                             String phoneNumber,
                             String role,
                             String status,
                             String sortBy,
                             String sortOrder,
                             int page,
                             int size
    ) {
        this.username = sanitizeOptional(username);
        this.email = sanitizeOptional(email);
        this.phoneNumber = sanitizeOptional(phoneNumber);
        this.role = validateRole(role);
        this.status = validateAccountStatus(status);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;
    }

    private Optional<String> sanitizeOptional(String value) {
        return (value == null || value.trim().isEmpty()) ? Optional.empty() : Optional.of(value);
    }

    private Optional<String> validateRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return Optional.empty();
        }
        List<String> validRoles = Arrays.stream(Role.values())
                .map(Enum::name)
                .toList();
        return validRoles.contains(role) ? Optional.of(role) : Optional.empty();
    }

    private Optional<String> validateAccountStatus(String accountStatus) {
        if (accountStatus == null || accountStatus.trim().isEmpty()) {
            return Optional.empty();
        }

        List<String> validAccountStatuses = Arrays.stream(AccountStatus.values())
                .map(Enum::name)
                .toList();
        return validAccountStatuses.contains(accountStatus) ? Optional.of(accountStatus) : Optional.empty();
    }

    private String validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("username", "email", "phoneNumber", "role", "status", "date");
        return (sortBy != null && !sortBy.isEmpty() && validFields.contains(sortBy)) ? sortBy : "date";
    }

    private String validateSortOrder(String sortOrder) {
        return (sortOrder != null &&
                !sortOrder.isEmpty() &&
                (sortOrder.equals("asc") || sortOrder.equals("desc"))) ? sortOrder : "desc";
    }
}