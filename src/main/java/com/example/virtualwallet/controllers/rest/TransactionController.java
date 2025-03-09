package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile/transactions")
@Tag(name = "Transaction Management", description = "API for managing user transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

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

    @Operation(
            summary = "Retrieve all of user's transactions",
            description = "Fetch a list of user's transactions to other users.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping
    public ResponseEntity<List<TransactionOutput>> getTransactions() {
        return ResponseEntity.ok(transactionService
                .findAllTransactionsByUserId(userService.getAuthenticatedUser().getId()));
    }

    @Operation(
            summary = "Retrieve all of user's transactions with filter options",
            description = "Fetch a list of user's transactions to other users with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/filter")
    public ResponseEntity<?> getTransactionsWithFilter(@RequestParam(required = false) String firstDate,
                                                       @RequestParam(required = false) String lastDate,
                                                       @RequestParam(required = false) String currency,
                                                       @RequestParam(required = false) String sender,
                                                       @RequestParam(required = false) String recipient,
                                                       @RequestParam(required = false) String direction,
                                                       @RequestParam(defaultValue = "date") String sortBy,
                                                       @RequestParam(defaultValue = "desc") String sortOrder,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
        }
        TransactionFilterOptions transactionFilterOptions = new TransactionFilterOptions
                (firstDate, lastDate, currency, sender, recipient, direction, sortBy, sortOrder, page, size);
        UUID userId = userService.getAuthenticatedUser().getId();

        List<TransactionOutput> result =
                transactionService.findAllTransactionsByUserIdWithFilters(userId, transactionFilterOptions);
        return ResponseEntity.ok(result);
    }
}
