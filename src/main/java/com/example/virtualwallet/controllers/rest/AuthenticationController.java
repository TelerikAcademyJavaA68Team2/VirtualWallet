package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.dtos.auth.NewPasswordResetInput;
import com.example.virtualwallet.models.dtos.auth.PasswordResetInput;
import com.example.virtualwallet.services.contracts.AuthenticationService;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.models.dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.dtos.auth.RegisterUserInput;
import com.example.virtualwallet.services.contracts.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Management", description = "APIs for managing user authentication, registration " +
        "and email confirmation")
public class AuthenticationController {

    public static final String CONFIRMED_SUCCESSFULLY = "Email confirmed successfully";
    public static final String CHANGED_SUCCESSFULLY = "Password Changed Successfully";
    public static final String EMAIL_SEND_SUCCESSFULLY = "Email send successfully";

    private final AuthenticationService authService;
    private final PasswordResetService passwordResetService;
    private final EmailConfirmationService emailConfirmationService;

    @Operation(summary = "Register a New User", description = "Register a new user account",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Conflict - Duplicate user", responseCode = "409"),
                    @ApiResponse(description = "Bad Request - Invalid user input", responseCode = "400")
            })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserInput request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "User Login", description = "Authenticate an existing user",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request - User not found", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized - Account blocked", responseCode = "401")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserInput request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @Operation(
            description = "Confirm user email address",
            summary = "Confirm email address of logged in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = CONFIRMED_SUCCESSFULLY),
                    @ApiResponse(responseCode = "404", description = "Email confirmation token not found"),
                    @ApiResponse(responseCode = "400", description = "Email confirmation token expired"),
                    @ApiResponse(responseCode = "400", description = "Email is already confirmed"),
            }
    )
    @GetMapping("/email-confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam UUID token) {
        try {
            emailConfirmationService.confirmEmailToken(token);
            return ResponseEntity.ok(CONFIRMED_SUCCESSFULLY);
        } catch (EmailConfirmationException | EmailConfirmedException | DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(
            description = "Send password-reset email",
            summary = "Send a email to a valid user email address with a link containing the UUID of the resetPasswordToken.",
            responses = {
                    @ApiResponse(responseCode = "200", description = EMAIL_SEND_SUCCESSFULLY),
                    @ApiResponse(responseCode = "404", description = "User with provided email was not found"),
                    @ApiResponse(responseCode = "400", description = "Email confirmation token was already sent less than 15min ago"),
            }
    )
    @PostMapping("/password-reset")
    public ResponseEntity<String> processPasswordResetInput(@Valid @RequestBody PasswordResetInput input) {
        try {
            passwordResetService.sendResetPasswordEmail(input, true);
            return ResponseEntity.ok(EMAIL_SEND_SUCCESSFULLY);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (EmailConfirmationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "Process new password-input",
            summary = "Verify the password input make sure the new password is confirmed correctly and the UUID of the token is valid.",
            responses = {
                    @ApiResponse(responseCode = "200", description = CHANGED_SUCCESSFULLY),
                    @ApiResponse(responseCode = "404", description = "The UUID provided was not found"),
                    @ApiResponse(responseCode = "400", description = "The password confirmation failed"),
            }
    )
    @PostMapping("/password-reset/{id}")
    public ResponseEntity<String> processPasswordResetInput(@PathVariable UUID id, @Valid @RequestBody NewPasswordResetInput input) {
        try {
            passwordResetService.processResetPasswordInput(input, id);
            return ResponseEntity.ok(CHANGED_SUCCESSFULLY);
        } catch (InvalidUserInputException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}