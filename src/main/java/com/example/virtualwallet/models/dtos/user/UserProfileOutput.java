package com.example.virtualwallet.models.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileOutput {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String photo;
    private String role;
    private String accountStatus;
    private String registeredAt;
}