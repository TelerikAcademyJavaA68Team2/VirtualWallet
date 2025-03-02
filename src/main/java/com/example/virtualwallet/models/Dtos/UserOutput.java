package com.example.virtualwallet.models.Dtos;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UserOutput {
    private UUID id;
    private String username;
    private String email;
    private String phoneNumber;
    private Role role;
    private AccountStatus accountStatus;
    private BigDecimal totalBalance;

    public UserOutput(UUID id, String username, String email, String phoneNumber, Role role, AccountStatus accountStatus, BigDecimal totalBalance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.accountStatus = accountStatus;
        this.totalBalance = totalBalance;
    }
}