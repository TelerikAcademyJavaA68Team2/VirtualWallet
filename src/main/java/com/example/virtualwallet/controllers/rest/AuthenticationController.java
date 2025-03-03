package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.auth.AuthenticationService;
import com.example.virtualwallet.auth.emailVerification.EmailConfirmationService;
import com.example.virtualwallet.exceptions.EmailConfirmationException;
import com.example.virtualwallet.models.dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.dtos.auth.RegisterUserInput;
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
public class AuthenticationController {

    private final AuthenticationService authService;
    private final EmailConfirmationService emailConfirmationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserInput request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserInput request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/register/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam UUID tokenId) {
        try {
            emailConfirmationService.confirmEmailToken(tokenId);
            return ResponseEntity.ok("Email confirmed successfully");
        } catch (EmailConfirmationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }


}