package com.example.virtualwallet.models.Dtos;

import lombok.Data;

@Data
public class UserOutput {

    private String username;
    private String email;
    private String phoneNumber;
    private String role;
    private boolean isEnabled;
    private long transactionCount;

    public UserOutput(String username, String email, String phoneNumber, String role, boolean isEnabled, long transactionCount) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isEnabled = isEnabled;
        this.transactionCount = transactionCount;
    }
}