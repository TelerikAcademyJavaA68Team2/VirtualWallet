package com.example.virtualwallet.models.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.MultipartFile;

@Data
@PropertySource("classpath:messages.properties")
public class ProfileUpdateInput {

    @Size(min = 2, max = 32, message = "{error.firstNameLength}")
    private String firstName;

    @Size(min = 2, max = 32, message = "{error.lastNameLength}")
    private String lastName;

    @Email(message = "{error.emailInvalid}")
    @Size(min = 5, max = 200, message = "{error.emailLength}")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "{error.phoneDigits}")
    private String phoneNumber;

    private MultipartFile profilePicture;
}