package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.card.CardEdit;
import com.example.virtualwallet.models.dtos.card.CardInput;
import com.example.virtualwallet.models.dtos.card.CardOutput;
import com.example.virtualwallet.models.dtos.card.CardOutputForList;
import com.example.virtualwallet.services.contracts.CardService;
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

@RestController
@RequestMapping("/api/profile/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Endpoints for user card interactions like add/update/remove a card")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @Operation(
            summary = "Retrieve all of user's cards",
            description = "Fetch a list of user's cards",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping
    public ResponseEntity<List<CardOutputForList>> getCards() {
        return ResponseEntity.ok(cardService.getAllCardsOutputForListByUser(userService.getAuthenticatedUser().getId()));
    }

    @Operation(
            description = "Retrieve a card by its ID",
            summary = "Get card by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved card"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @GetMapping("/{cardId}")
    public ResponseEntity<CardOutput> getCardById(@PathVariable UUID cardId) {
        return ResponseEntity.ok(cardService.getCardOutputById(cardId));
    }

    @Operation(
            description = "Add a new card",
            summary = "Add a card",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully added a card"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized to add a card"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "409", description = "Duplicate entity conflict")
            }
    )
    @PostMapping
    public ResponseEntity<CardOutput> addCard(@Valid @RequestBody CardInput cardInput){
        return ResponseEntity.ok(cardService.addCard(cardInput));
    }


    @Operation(
            description = "Update an existing card by optionally deciding which field you want to update:" +
                    "card number, cardholder, expiration date, or CVV",
            summary = "Update a card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated card"),
                    @ApiResponse(responseCode = "404", description = "Card or user not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized to update card"),
                    @ApiResponse(responseCode = "409", description = "Duplicate entity conflict")
            }
    )
    @PutMapping("/{cardId}")
    public ResponseEntity<CardOutput> updateCardDetails(@PathVariable UUID cardId,
                                  @Valid @RequestBody CardEdit cardEdit) {
        return ResponseEntity.ok(cardService.updateCard(cardEdit, cardId));
    }

    @Operation(
            description = "Delete a card by ID",
            summary = "Delete a card",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully marked card as deleted"),
                    @ApiResponse(responseCode = "404", description = "Card or user not found"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized to delete this card")
            }
    )
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID cardId){
        cardService.softDeleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
