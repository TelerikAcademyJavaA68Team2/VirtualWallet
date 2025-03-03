package com.example.virtualwallet.models.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource("classpath:messages.properties")
public class RegisterUserInput {

    @NotBlank(message = "First name cant be empty!")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols!")
    private String firstName;

    @NotBlank(message = "Last name cant be empty!")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols!")
    private String lastName;

    @NotBlank(message = "Email address is required!")
    @Email(message = "Email address is invalid!")
    @Size(min = 5, max = 200, message = "Email should be between 5 and 200 symbols!")
    private String email;

    @NotBlank(message = "Username is required!")
    @Size(min = 2, max = 20, message = "Username should be between 2 and 20 symbols!")
    private String username;

    @NotBlank(message = "Password is required!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[+\\-*&^%$#@!])[A-Za-z\\d+\\-*&^%$#@!]{8,}$",
            message = "Password must be at least 8 characters long and include a capital letter, a digit, and a special symbol!")
    @Size(min = 8, max = 40, message = "Password should be between 8 and 40 symbols!")
    private String password;

    @NotBlank(message = "Password confirmation is required!")
    private String passwordConfirm;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits.")
    private String phoneNumber;

}
