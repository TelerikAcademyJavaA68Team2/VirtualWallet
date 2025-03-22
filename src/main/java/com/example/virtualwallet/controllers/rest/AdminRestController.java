package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.exchange.ExchangePage;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.dtos.transactions.FullTransactionInfoOutput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransfersPage;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.services.contracts.ExchangeService;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.requestIsWithInvalidPageOrSize;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Endpoints for admin actions like user and transaction management")
public class AdminRestController {

    public static final String INVALID_PAGE_OR_SIZE_PARAMETERS = "Invalid page or size parameters.";
    public static final String NO_TRANSACTIONS = "No transactions with this filters!";
    public static final String NO_TRANSFERS = "No transfers with these filters!";

    private final UserService userService;
    private final TransactionService transactionService;
    private final TransferService transferService;
    private final ExchangeService exchangeService;

    @Operation(summary = "Retrieve user by ID", description = "Get a user profile",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request - User not found", responseCode = "400"),
            }
    )
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserInfoById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.getUserProfileById(id), HttpStatus.OK);
    }

    @Operation(summary = "Promote to admin", description = "Promote a given user to admin",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request - User not found", responseCode = "400"),
                    @ApiResponse(description = "User is already admin", responseCode = "400"),
            }
    )
    @PostMapping("/users/{id}/make-admin")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable UUID id) {
        userService.promoteToAdmin(id);
        return new ResponseEntity<>("Admin promotion was successful", HttpStatus.OK);
    }

    @Operation(summary = "Revoke admin rights", description = "Demote an admin to user",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request - User not found", responseCode = "400"),
                    @ApiResponse(description = "User is already with user rights", responseCode = "400"),
            }
    )
    @PostMapping("/users/{id}/revoke-admin")
    public ResponseEntity<?> demoteAdminToUser(@PathVariable UUID id) {
        userService.demoteToUser(id);
        return new ResponseEntity<>("Admin demoted successfully", HttpStatus.OK);
    }

    @Operation(summary = "Block a desired user", description = "Block a desired user by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request - User not found", responseCode = "400"),
                    @ApiResponse(description = "User is already blocked", responseCode = "400"),
            }
    )
    @PostMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable UUID id) {
        userService.blockUser(id);
        return new ResponseEntity<>("Block was successful", HttpStatus.OK);
    }

    @Operation(summary = "Unblock a desired user", description = "Unblock a desired user by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request - User not found", responseCode = "400"),
                    @ApiResponse(description = "User is not blocked", responseCode = "400"),
            }
    )
    @PostMapping("/users/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable UUID id) {
        userService.unblockUser(id);
        return new ResponseEntity<>("Unblock was successful", HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve all users with filter options",
            description = "Fetch a list of all users with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/users")
    public ResponseEntity<?> filterUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (requestIsWithInvalidPageOrSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        UserFilterOptions userFilterOptions = new UserFilterOptions(
                username,
                email,
                phoneNumber,
                role,
                status,
                fromDate,
                toDate,
                sortBy,
                sortOrder,
                page,
                size
        );

        UserPageOutput result = userService.filterUsers(userFilterOptions);
        if (result.getContent().isEmpty()) {
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Retrieve all transactions with filter options",
            description = "Fetch a list of all application transactions with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactionsWithFilter(
            @RequestParam(required = false) String specificUser,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (requestIsWithInvalidPageOrSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        TransactionFilterOptions transactionFilterOptions = new TransactionFilterOptions(
                specificUser,
                fromDate,
                toDate,
                minAmount,
                maxAmount,
                currency,
                sender,
                recipient,
                null,
                description,
                sortBy,
                sortOrder,
                page,
                size
        );

        List<TransactionOutput> result = transactionService.filterTransactions(transactionFilterOptions);
        if (result.isEmpty()) {
            return new ResponseEntity<>(NO_TRANSACTIONS, HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Retrieve all application transfers with filter options",
            description = "Fetch a list of transfers with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/transfers")
    public ResponseEntity<?> getAllTransfersWithFilters(
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }

        TransferFilterOptions filterOptions = new TransferFilterOptions(
                recipient,
                fromDate,
                toDate,
                minAmount,
                maxAmount,
                currency,
                status,
                sortBy, sortOrder, page, size
        );

       TransfersPage result = transferService.filterTransfers(filterOptions);

        if (result.getTransfers().isEmpty()) {
            return new ResponseEntity<>(NO_TRANSFERS, HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }


    @Operation(
            summary = "Retrieve all application exchanges with filter options",
            description = "Fetch a list of all exchanges with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/exchanges")
    public ResponseEntity<?> getExchangesAndFilter(@RequestParam(required = false) String fromDate,
                                                   @RequestParam(required = false) String toDate,
                                                   @RequestParam(required = false) String fromCurrency,
                                                   @RequestParam(required = false) String toCurrency,
                                                   @RequestParam(required = false) BigDecimal minStartAmount,
                                                   @RequestParam(required = false) BigDecimal maxStartAmount,
                                                   @RequestParam(required = false) BigDecimal minEndAmount,
                                                   @RequestParam(required = false) BigDecimal maxEndAmount,
                                                   @RequestParam(required = false) String recipientUsername,
                                                   @RequestParam(defaultValue = "date") String sortBy,
                                                   @RequestParam(defaultValue = "desc") String sortOrder,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions(
                fromDate,
                toDate,
                fromCurrency,
                toCurrency,
                minStartAmount,
                maxStartAmount,
                minEndAmount,
                maxEndAmount,
                recipientUsername,
                sortBy,
                sortOrder,
                page,
                size);

        ExchangePage result = exchangeService.filterExchanges(filterOptions);
        if (result.getExchanges().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(
            description = "Retrieve a transfer by its ID",
            summary = "Get transfer by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved transfer"),
                    @ApiResponse(responseCode = "404", description = "Transfer not found")
            }
    )
    @GetMapping("/transfers/{id}")
    public FullTransferInfoOutput getFullTransferById(@PathVariable UUID id) {
        return transferService.getTransferById(id);
    }

    @Operation(
            description = "Retrieve a transaction by its ID",
            summary = "Get transaction by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction"),
                    @ApiResponse(responseCode = "404", description = "Transaction not found")
            }
    )
    @GetMapping("/transactions/{id}")
    public FullTransactionInfoOutput getTransferById(@PathVariable UUID id) {
        return transactionService.getTransactionById(id);
    }

    @Operation(
            description = "Retrieve an exchange by its ID",
            summary = "Get exchange by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange"),
                    @ApiResponse(responseCode = "404", description = "Exchange not found")
            }
    )
    @GetMapping("/exchanges/{id}")
    public FullExchangeInfoOutput getExchangeById(@PathVariable UUID id) {
        return exchangeService.getExchangeById(id);
    }

}