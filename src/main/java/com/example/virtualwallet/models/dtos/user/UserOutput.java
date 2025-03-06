package com.example.virtualwallet.models.dtos.user;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserOutput {
    private UUID id;
    private String photo;
    private String username;
    private String email;
    private String phoneNumber;
    private Role role;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
}