package com.example.virtualwallet.models.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewPasswordResetInput {

    @NotBlank(message = "{error.password}")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$",
            message = "{error.passwordValidation}")
    @Size(min = 8, max = 40, message = "{error.passwordLength}")
    private String password;

    @NotBlank(message = "{error.passwordConfirmation}")
    private String passwordConfirm;
}
