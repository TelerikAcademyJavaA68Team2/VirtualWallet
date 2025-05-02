package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.dtos.transfer.TransfersPage;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import com.example.virtualwallet.services.contracts.TransferService;
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
import java.util.Random;
import java.util.UUID;

import static com.example.virtualwallet.controllers.rest.AdminRestController.INVALID_PAGE_OR_SIZE_PARAMETERS;
import static com.example.virtualwallet.helpers.ValidationHelpers.requestIsWithInvalidPageOrSize;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile/transfers")
@Tag(name = "Transfer Management", description = "API for making user transfers")
public class TransferController {

    public static final String NO_TRANSFERS = "No transfers with these filters!";

    private final TransferService transferService;
    private final UserService userService;


    @Operation(
            summary = "Retrieve all of user's transfers with filter options",
            description = "Fetch a list of user's transfers to his wallet with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping
    public ResponseEntity<?> getTransfersWithFilter(
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
        if (requestIsWithInvalidPageOrSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }

        TransferFilterOptions filterOptions = new TransferFilterOptions(
                userService.getAuthenticatedUser().getUsername(),
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
            description = "Retrieve a transfer by its ID",
            summary = "Get transfer by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved transfer"),
                    @ApiResponse(responseCode = "404", description = "Transfer not found")
            }
    )
    @GetMapping("/{id}")
    public FullTransferInfoOutput getFullTransferById(@PathVariable UUID id) {
        return transferService.getTransferById(id);
    }

    @Operation(
            summary = "Make a transfer",
            description = "Make a transfer from a user's card to the wallet of the specified currency. " +
                    "Automatically creates a wallet if the user doesn't have any of that currency.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully made a withdrawal"),
                    @ApiResponse(responseCode = "400", description = "Invalid input Data"),
                    @ApiResponse(responseCode = "400", description = "Card has expired"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "Entity not found")
            }
    )
    @PostMapping("/new")
    public ResponseEntity<FullTransferInfoOutput> makeTransfer(@Valid @RequestBody TransferInput transferInput) {
        return ResponseEntity.ok(transferService.processTransfer(transferInput));
    }

    @Operation(
            summary = "Randomly returns a true/false boolean to confirm/decline a transfer's withdraw status",
            description = "Make an additional call to /withdraw api to confirm a transfer's status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully returned a boolean which " +
                            "determines if the transfer is a approved or declined"),
            }
    )
    @GetMapping("/withdraw")
    public ResponseEntity<Boolean> withdraw() {
        return ResponseEntity.ok(new Random().nextBoolean());
    }
}