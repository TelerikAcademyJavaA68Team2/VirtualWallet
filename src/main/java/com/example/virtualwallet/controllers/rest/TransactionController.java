package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.transactions.FullTransactionInfoOutput;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.controllers.rest.AdminRestController.INVALID_PAGE_OR_SIZE_PARAMETERS;
import static com.example.virtualwallet.helpers.ValidationHelpers.validatePageOrSize;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile/transactions")
@Tag(name = "Transaction Management", description = "API for managing user transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @Operation(
            summary = "Retrieve all of user's transactions with filter options",
            description = "Fetch a list of user's transactions to other users with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping
    public ResponseEntity<?> getTransactionsWithFilter(@RequestParam(required = false) String fromDate,
                                                       @RequestParam(required = false) String toDate,
                                                       @RequestParam(required = false) BigDecimal minAmount,
                                                       @RequestParam(required = false) BigDecimal maxAmount,
                                                       @RequestParam(required = false) String currency,
                                                       @RequestParam(required = false) String sender,
                                                       @RequestParam(required = false) String recipient,
                                                       @RequestParam(required = false) String direction,
                                                       @RequestParam(required = false) String description,
                                                       @RequestParam(defaultValue = "date") String sortBy,
                                                       @RequestParam(defaultValue = "desc") String sortOrder,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        if (validatePageOrSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }

        User user = userService.getAuthenticatedUser();
        TransactionFilterOptions transactionFilterOptions = new TransactionFilterOptions(
                user.getUsername(),
                fromDate,
                toDate,
                minAmount,
                maxAmount,
                currency,
                sender,
                recipient,
                direction,
                description,
                sortBy,
                sortOrder,
                page,
                size
        );

        List<TransactionOutput> result =
                transactionService.filterTransactions(transactionFilterOptions);

        if (result.isEmpty()) {
            return new ResponseEntity<>("No transactions with this filters!", HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(
            description = "Retrieve a transaction by its ID",
            summary = "Get transaction by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction"),
                    @ApiResponse(responseCode = "404", description = "Transaction not found")
            }
    )
    @GetMapping("/{id}")
    public FullTransactionInfoOutput getFullTransactionInfoById(@PathVariable UUID id) {
        return transactionService.getTransactionById(id);
    }


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
    public ResponseEntity<TransactionOutput> makeTransaction(@Valid @RequestBody TransactionInput transactionInput) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionInput));

    }
}
