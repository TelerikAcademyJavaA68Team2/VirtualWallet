package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.auth.AuthenticationService;
import com.example.virtualwallet.auth.emailVerification.EmailConfirmationService;
import com.example.virtualwallet.exceptions.EmailConfirmationException;
import com.example.virtualwallet.models.dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.dtos.auth.RegisterUserInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Management", description = "APIs for managing authentication and registration")
public class AuthenticationController {

    private final AuthenticationService authService;
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

    @GetMapping("/register/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam UUID token) {
        try {
            emailConfirmationService.confirmEmailToken(token);
            return ResponseEntity.ok("Email confirmed successfully");
        } catch (EmailConfirmationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


}