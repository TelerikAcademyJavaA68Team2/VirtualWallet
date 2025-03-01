package com.example.virtualwallet.models.fillterOptions;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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


    public UserFilterOptions(String username,
                             String email,
                             String phoneNumber,
                             String role,
                             String accountStatus,
                             BigDecimal minTotalBalance,
                             BigDecimal maxTotalBalance
    ) {
        this.username = sanitizeOptional(username);
        this.email = sanitizeOptional(email);
        this.phoneNumber = sanitizeOptional(phoneNumber);
        this.role = sanitizeOptional(role);
        this.accountStatus = sanitizeOptional(accountStatus);
        this.minTotalBalance = Optional.ofNullable(minTotalBalance);
        this.maxTotalBalance = Optional.ofNullable(maxTotalBalance);
    }

    private Optional<String> sanitizeOptional(String value) {
        return (value == null || value.trim().isEmpty()) ? Optional.empty() : Optional.of(value);
    }
}