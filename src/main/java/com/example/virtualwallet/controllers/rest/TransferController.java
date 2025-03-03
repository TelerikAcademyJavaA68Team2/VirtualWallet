package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.TransferInput;
import com.example.virtualwallet.models.dtos.TransferOutput;
import com.example.virtualwallet.services.contracts.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transfer")
@Tag(name = "Transfer Management", description = "API for making user transfers")
public class TransferController {

    private final TransferService transferService;

    @Operation(
            summary = "Make a withdraw",
            description = "Make a withdraw from a user's card to the wallet of the specified currency. " +
                    "Automatically creates a wallet if the user doesn't have any of that currency.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully made a withdrawal"),
                    @ApiResponse(responseCode = "400", description = "Invalid input Data"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "Entity not found")
            }
    )
    @PostMapping("/withdraw")
    public ResponseEntity<TransferOutput> withdraw(@Valid @RequestBody TransferInput transferInput) {
        return ResponseEntity.ok(transferService.processTransfer(transferInput));
    }
}
