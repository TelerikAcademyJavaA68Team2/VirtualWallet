package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.TransactionInput;
import com.example.virtualwallet.models.dtos.TransactionOutput;
import com.example.virtualwallet.services.contracts.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "API for managing user transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Make a transaction",
            description = "Make a transaction from user to user, by providing username, email or phone number, . " +
                    "Automatically creates a wallet if the user doesn't have any of that currency. " +
                    "Throws and exception if the sender has insufficient funds in his wallet",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully made a transaction"),
                    @ApiResponse(responseCode = "400", description = "Invalid input Data"),
                    @ApiResponse(responseCode = "400", description = "Insufficient Funds in your wallet"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
            }
    )
    @PostMapping("/new")
    public ResponseEntity<TransactionOutput> makeTransaction(@Valid @RequestBody TransactionInput transactionInput){
        return ResponseEntity.ok(transactionService.createTransaction(transactionInput));

    }
}
