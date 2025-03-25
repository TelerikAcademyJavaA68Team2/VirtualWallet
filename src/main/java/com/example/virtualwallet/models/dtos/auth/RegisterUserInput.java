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

    @NotBlank(message = "{error.firstNameBlank}")
    @Size(min = 2, max = 32, message = "{error.firstNameLength}")
    private String firstName;

    @NotBlank(message = "{error.lastNameBlank}")
    @Size(min = 2, max = 32, message = "{error.lastNameLength}")
    private String lastName;

    @NotBlank(message = "{error.emailRequired}")
    @Email(message = "{error.emailInvalid}")
    @Size(min = 5, max = 50, message = "{error.emailLength}")
    private String email;

    @NotBlank(message = "{error.username}")
    @Size(min = 2, max = 20, message = "{error.usernameLength}")
    private String username;

    @NotBlank(message = "{error.password}")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$",
            message = "{error.passwordValidation}")
    @Size(min = 8, max = 40, message = "{error.passwordLength}")
    private String password;

    @NotBlank(message = "{error.passwordConfirmation}")
    private String passwordConfirm;

    @NotBlank(message = "{error.phone}")
    @Pattern(regexp = "\\d{10}", message = "{error.phoneDigits}")
    private String phoneNumber;

}
