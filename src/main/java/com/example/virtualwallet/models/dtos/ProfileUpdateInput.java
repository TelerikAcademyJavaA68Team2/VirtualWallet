package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateInput {

    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols!")
    private String firstName;

    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols!")
    private String lastName;

    @Email(message = "Email address is invalid!")
    @Size(min = 5, max = 200, message = "Email should be between 5 and 200 symbols!")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits.")
    private String phoneNumber;

    @NotBlank(message = "Your current password is required for any profile updates!")
    private String password;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[+\\-*&^%$#@!])[A-Za-z\\d+\\-*&^%$#@!]{8,}$",
            message = "Password must be at least 8 characters long and include a capital letter, a digit, and a special symbol!")
    @Size(min = 8, max = 40, message = "Password should be between 8 and 40 symbols!")
    private String newPassword;

    private String newPasswordConfirm;
}