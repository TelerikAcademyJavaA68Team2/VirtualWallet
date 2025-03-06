package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.TransactionOutput;
import com.example.virtualwallet.models.dtos.TransferOutput;
import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "API for managing user profile, " +
        "listing cards, transactions and transfers")
public class ProfileController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final TransferService transferService;

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

    @Operation(
            summary = "Retrieve all of user's transactions",
            description = "Fetch a list of user's transactions to other users.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionOutput>> getTransactions() {
        return ResponseEntity.ok(transactionService
                .findAllTransactionsByUserId(userService.getAuthenticatedUser().getId()));
    }

    @Operation(
            summary = "Retrieve all of user's transfers",
            description = "Fetch a list of user's transfers to fund his cards.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/transfers")
    public ResponseEntity<List<TransferOutput>> getTransfers() {
        return ResponseEntity.ok(transferService.
                findAllTransfersByUserId(userService.getAuthenticatedUser()));
    }
}
