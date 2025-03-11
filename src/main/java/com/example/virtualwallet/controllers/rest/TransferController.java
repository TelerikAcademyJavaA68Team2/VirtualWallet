package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;
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
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile/transfers")
@Tag(name = "Transfer Management", description = "API for making user transfers")
public class TransferController {

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
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
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

        List<TransferOutput> result = transferService.filterTransfers(filterOptions);

        if (result.isEmpty()) {
            return new ResponseEntity<>("No transfers with these filters!", HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

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
                    @ApiResponse(responseCode = "200", description = "Successfully returned a boolean"),
            }
    )
    @GetMapping("/withdraw")
    public ResponseEntity<Boolean> withdraw() {
        return ResponseEntity.ok(new Random().nextBoolean());
    }

}