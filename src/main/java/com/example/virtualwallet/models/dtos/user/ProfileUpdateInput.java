package com.example.virtualwallet.models.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource("classpath:messages.properties")
public class ProfileUpdateInput {

    @Size(min = 4, max = 32, message = "{error.firstNameLength}")
    private String firstName;

    @Size(min = 4, max = 32, message = "{error.lastNameLength}")
    private String lastName;

    @Email(message = "{error.emailInvalid}")
    @Size(min = 5, max = 200, message = "{error.emailLength}")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "{error.phoneDigits}")
    private String phoneNumber;

    @NotBlank(message = "{error.passwordRequired}")
    private String password;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[+\\-*&^%$#@!])[A-Za-z\\d+\\-*&^%$#@!]{8,}$",
            message = "{error.passwordValidation}")
    @Size(min = 8, max = 40, message = "{error.passwordLength}")
    private String newPassword;

    private String newPasswordConfirm;
}