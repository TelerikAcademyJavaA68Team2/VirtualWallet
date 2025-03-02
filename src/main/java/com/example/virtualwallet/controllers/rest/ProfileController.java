package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.UserProfileOutput;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @DeleteMapping
    public ResponseEntity<String> deleteProfile() {
        userService.softDeleteAuthenticatedUser();
        return ResponseEntity.ok("Account deleted successfully");
    }

    @GetMapping
    public ResponseEntity<UserProfileOutput> getProfile() {
        return ResponseEntity.ok( userService.getAuthenticatedUserProfile());
    }
}
