package com.example.virtualwallet.models.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateInput {

    @NotBlank(message = "{error.passwordRequired}")
    private String password;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[+\\-*&^%$#@!])[A-Za-z\\d+\\-*&^%$#@!]{8,}$",
            message = "{error.passwordValidation}")
    @Size(min = 8, max = 40, message = "{error.passwordLength}")
    private String newPassword;

    private String newPasswordConfirm;
}