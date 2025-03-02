package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserOutput {
    private UUID id;
    private String username;
    private String email;
    private String phoneNumber;
    private Role role;
    private AccountStatus accountStatus;
    private BigDecimal totalBalance;
}