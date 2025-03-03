package com.example.virtualwallet.models.dtos.user;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserProfileOutput {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private Role role;
    private AccountStatus accountStatus;
    private LocalDateTime registeredAt;
}