package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Getter
public class UserFilterOptions {

    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<Role> role;
    private Optional<AccountStatus> status;
    private Optional<LocalDateTime> fromDate;
    private Optional<LocalDateTime> toDate;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int size;

    public UserFilterOptions(String username,
                             String email,
                             String phoneNumber,
                             String role,
                             String status,
                             String fromDate,
                             String toDate,
                             String sortBy,
                             String sortOrder,
                             int page,
                             int size
    ) {
        this.username = sanitizeOptional(username);
        this.email = sanitizeOptional(email);
        this.phoneNumber = sanitizeOptional(phoneNumber);
        this.role = sanitizeRole(role);
        this.status = sanitizeAccountStatus(status);
        this.fromDate = parseLocalDateTime(fromDate);
        this.toDate = parseLocalDateTime(toDate);
        this.sortBy = validateSortBy(sortBy);
        this.sortOrder = validateSortOrder(sortOrder);
        this.page = page;
        this.size = size;
    }

    private String validateSortBy(String sortBy) {
        List<String> validFields = Arrays.asList("username", "email", "phoneNumber", "role", "status", "createdAt");
        return (sortBy != null && !sortBy.isEmpty() && validFields.contains(sortBy)) ? sortBy : "createdAt";
    }
}