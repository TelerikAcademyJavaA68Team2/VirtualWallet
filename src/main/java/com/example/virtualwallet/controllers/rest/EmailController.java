package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.exceptions.EmailConfirmationException;
import com.example.virtualwallet.exceptions.EmailConfirmedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/email")
public class EmailController {

    private final EmailConfirmationService emailConfirmationService;

    //ToDo register confirm swagger documentation
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam UUID token) {
        try {
            emailConfirmationService.confirmEmailToken(token);
            return ResponseEntity.ok("Email confirmed successfully");
        } catch (EmailConfirmationException | EmailConfirmedException | DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}