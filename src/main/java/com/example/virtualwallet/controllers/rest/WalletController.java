package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.services.contracts.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.virtualwallet.controllers.rest.AdminRestController.INVALID_PAGE_OR_SIZE_PARAMETERS;
import static com.example.virtualwallet.helpers.ValidationHelpers.requestIsWithInvalidPageOrSize;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/wallets")
@Tag(name = "Wallet Management", description = "API for managing user's wallets")
public class WalletController {

    public static final String DELETED = "Wallet deleted";

    private final WalletService walletService;

    @Operation(
            summary = "Retrieve all of user's wallets",
            description = "Fetch a list of user's wallets",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping
    public ResponseEntity<?> getAllWallets(@RequestParam(defaultValue = "BGN") String mainCurrency,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {

        if (requestIsWithInvalidPageOrSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        return new ResponseEntity<>(walletService.getActiveWalletsOfAuthenticatedUser(mainCurrency, page, size), HttpStatus.OK);
    }

    @Operation(
            summary = "Create a new wallet",
            description = "Create a wallet by providing its currency",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Duplicate wallet with currency", responseCode = "409")
            }
    )
    @PostMapping
    public ResponseEntity<?> createWallet(@RequestParam String currency) {
        walletService.createAuthenticatedUserWalletWalletByCurrency(currency);
        return new ResponseEntity<>(currency + " Wallet created successfully!", HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a wallet",
            description = "Delete a wallet by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Wallet not found", responseCode = "404"),
                    @ApiResponse(description = "Unauthorized to remove wallet", responseCode = "401"),
                    @ApiResponse(description = "There are still remaining funds in your wallet and cannot be removed"
                            , responseCode = "400"),
            }
    )
    @DeleteMapping("/{walletId}")
    public ResponseEntity<?> deleteWallet(@PathVariable UUID walletId) {
        walletService.softDeleteAuthenticatedUserWalletById(walletId);
       return ResponseEntity.ok(DELETED);
    }

    @Operation(
            summary = "Get a wallet's history",
            description = "Fetch a list of all wallet activities",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Wallet not found", responseCode = "404")
            }
    )
    @GetMapping("/{walletId}")
    public ResponseEntity<?> getWalletHistory(@PathVariable UUID walletId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {

        if (requestIsWithInvalidPageOrSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        return new ResponseEntity<>(walletService.getWalletPageById(walletId, page, size), HttpStatus.OK);
    }


}
