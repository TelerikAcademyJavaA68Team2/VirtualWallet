package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.exchange.ExchangeOutput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;
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
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.validPageAndSize;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Endpoints for admin actions like user, post, and comment management")
public class AdminRestController {

    public static final String INVALID_PAGE_OR_SIZE_PARAMETERS = "Invalid page or size parameters.";

    private final UserService userService;
    private final TransactionService transactionService;
    private final TransferService transferService;
    private final ExchangeService exchangeService;


    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserInfoById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.getUserProfileById(id), HttpStatus.OK);
    }

    @PostMapping("/users/{id}/make-admin")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable UUID id) {
        userService.promoteToAdmin(id);
        return new ResponseEntity<>("Admin promotion was successful", HttpStatus.OK);
    }

    @PostMapping("/users/{id}/revoke-admin")
    public ResponseEntity<?> demoteAdminToUser(@PathVariable UUID id) {
        userService.demoteToUser(id);
        return new ResponseEntity<>("Admin demoted successfully", HttpStatus.OK);
    }

    @PostMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable UUID id) {
        userService.blockUser(id);
        return new ResponseEntity<>("Block was successful", HttpStatus.OK);
    }

    @PostMapping("/users/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable UUID id) {
        userService.unblockUser(id);
        return new ResponseEntity<>("Unblock was successful", HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<?> filterUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (validPageAndSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        UserFilterOptions userFilterOptions = new UserFilterOptions(
                username,
                email,
                phoneNumber,
                role,
                accountStatus,
                sortBy,
                sortOrder,
                page,
                size
        );
        return ResponseEntity.ok(userService.filterUsers(userFilterOptions));
    }

    @Operation(
            summary = "Retrieve all transactions with filter options",
            description = "Fetch a list of all transactions with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactionsWithFilter(@RequestParam(required = false) String fromDate,
                                                          @RequestParam(required = false) String toDate,
                                                          @RequestParam(required = false) BigDecimal minAmount,
                                                          @RequestParam(required = false) BigDecimal maxAmount,
                                                          @RequestParam(required = false) String currency,
                                                          @RequestParam(required = false) String sender,
                                                          @RequestParam(required = false) String recipient,
                                                          @RequestParam(defaultValue = "date") String sortBy,
                                                          @RequestParam(defaultValue = "desc") String sortOrder,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        if (validPageAndSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        TransactionFilterOptions transactionFilterOptions = new TransactionFilterOptions(
                null,
                fromDate,
                toDate,
                minAmount,
                maxAmount,
                currency,
                sender,
                recipient,
                null,
                sortBy,
                sortOrder,
                page,
                size
        );

        List<TransactionOutput> result = transactionService.filterTransactions(transactionFilterOptions);
        if (result.isEmpty()) {
            return new ResponseEntity<>("No transactions with this filters!", HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Retrieve all transfers with filter options",
            description = "Fetch a list of transfers wallets with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/transfers")
    public ResponseEntity<?> getAllTransfersWithFilters(
            @RequestParam(required = false) String firstDate,
            @RequestParam(required = false) String lastDate,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
        }

        // Build filter options WITHOUT userId => will fetch all transfers
        TransferFilterOptions filterOptions = new TransferFilterOptions(
                firstDate, lastDate, currency, status, cardNumber,
                sortBy, sortOrder, page, size
        );

        List<TransferOutput> result = transferService.findAllTransfersWithFilters(filterOptions);

        if (result.isEmpty()) {
            return new ResponseEntity<>("No transfers with these filters!", HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

//    @Operation(
//            summary = "Retrieve all exchanges with filter options",
//            description = "Fetch a list of exchanges wallets with " +
//                    "filter options, sorting and pagination.",
//            responses = {
//                    @ApiResponse(description = "Success", responseCode = "200")
//            }
//    )
//    @GetMapping("/exchanges")
//    public ResponseEntity<?> getAllExchangesWithFilters(
//                                             @RequestParam(defaultValue = "date") String sortBy,
//                                             @RequestParam(defaultValue = "desc") String sortOrder,
//                                             @RequestParam(defaultValue = "0") int page,
//                                             @RequestParam(defaultValue = "10") int size) {
//        if (page < 0 || size <= 0) {
//            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
//        }
//        ExchangeFilterOptions exchangeFilterOptions = new ExchangeFilterOptions(
//                sortBy, sortOrder, page, size);
//
//
//        List<ExchangeOutput> result =
//                exchangeService.findAllExchanges(transferFilterOptions);
//
//        if(result.isEmpty()){
//            return new ResponseEntity<>("No exchanges with this filters!", HttpStatus.NO_CONTENT);
//        }
//        return ResponseEntity.ok(result);
//    }


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
            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
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

        List<ExchangeOutput> result = exchangeService.filterExchanges(filterOptions);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

}

