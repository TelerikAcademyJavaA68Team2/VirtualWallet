package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping
    public ResponseEntity<String> updateProfile(@Valid @RequestBody ProfileUpdateInput input ) {
        userService.updateAuthenticatedUser(input);

        return ResponseEntity.ok("Account updated successfully");
    }
}
