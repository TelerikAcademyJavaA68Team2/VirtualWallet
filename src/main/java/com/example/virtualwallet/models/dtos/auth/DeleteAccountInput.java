package com.example.virtualwallet.models.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountInput {

    @NotBlank(message = "Wrong Password")
    private String password;

    @NotBlank(message = "Wrong Captcha")
    private String captcha;
}