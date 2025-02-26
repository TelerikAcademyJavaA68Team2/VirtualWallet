package com.example.virtualwallet.models.fillterOptions;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class UserFilterOptions {

    private Optional<String> phoneNumber;
    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> account_type;
    private Optional<String> account_status;
    private Optional<Integer> minNumberOfTransactions;
    private Optional<Integer> maxNumberOfTransactions;
    private Optional<String> orderBy;


    public UserFilterOptions(String phoneNumber,
                             String username,
                             String email,
                             String account_type,
                             String account_status,
                             Integer minNumberOfTransactions,
                             Integer maxNumberOfTransactions,
                             String orderBy) {
        this.phoneNumber = sanitizeOptional(phoneNumber);
        this.username = sanitizeOptional(username);
        this.email = sanitizeOptional(email);
        this.account_type = sanitizeOptional(account_type);
        this.account_status = sanitizeOptional(account_status);
        this.minNumberOfTransactions = Optional.ofNullable(minNumberOfTransactions);
        this.maxNumberOfTransactions = Optional.ofNullable(maxNumberOfTransactions);
        this.orderBy=sanitizeOptional(orderBy);
    }

    private Optional<String> sanitizeOptional(String value) {
        return (value == null || value.trim().isEmpty()) ? Optional.empty() : Optional.of(value);
    }
}