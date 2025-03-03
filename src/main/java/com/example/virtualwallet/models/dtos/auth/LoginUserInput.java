package com.example.virtualwallet.models.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource("classpath:messages.properties")
public class LoginUserInput {
    @NotBlank(message = "Username is required!")
    private String username;

    @NotBlank(message = "Password is required!")
    private String password;
}
