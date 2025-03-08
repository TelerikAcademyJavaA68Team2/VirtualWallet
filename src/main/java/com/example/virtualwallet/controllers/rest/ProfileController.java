package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "API for managing user profile")
public class ProfileController {

    private final UserService userService;

    @Operation(
            summary = "Delete user account",
            description = "Soft delete the account of the currently authenticated user after confirming login credentials.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
            }
    )
    @DeleteMapping
    public ResponseEntity<String> deleteProfile() {
        userService.softDeleteAuthenticatedUser();
        return ResponseEntity.ok("Account deleted successfully");
    }

    @Operation(
            summary = "Get user profile",
            description = "Retrieve the profile information of the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated")
            }
    )
    @GetMapping
    public ResponseEntity<UserProfileOutput> getProfile() {
        return ResponseEntity.ok(userService.getAuthenticatedUserProfile());
    }

    @Operation(
            summary = "Update user profile",
            description = "Update the profile information of the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "409", description = "Duplicate entity conflict"),
                    @ApiResponse(responseCode = "403", description = "User not authorized to update profile"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PatchMapping
    public ResponseEntity<String> updateProfile(@Valid @RequestBody ProfileUpdateInput input ) {
        userService.updateAuthenticatedUser(input);

        return ResponseEntity.ok("Account updated successfully");
    }
}
