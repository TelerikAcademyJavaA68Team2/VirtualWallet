package com.example.virtualwallet.models.dtos.auth;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class PasswordResetInput {

    @Email
    private String email;
}