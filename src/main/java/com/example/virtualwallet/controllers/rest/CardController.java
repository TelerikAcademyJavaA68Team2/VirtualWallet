package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.Dtos.CardInput;
import com.example.virtualwallet.models.Dtos.CardOutput;
import com.example.virtualwallet.models.Dtos.CardOutputForList;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/cards")
@Tag(name = "Card Management", description = "Endpoints for user card interactions like add/update/remove a card")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

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
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PostMapping
    public ResponseEntity<CardOutput> addCard(@Valid @RequestBody CardInput cardInput){
        return ResponseEntity.ok(cardService.addCard(cardInput));
    }

//    @PutMapping("/{cardId}")
//    @Operation(
//            description = "Update an existing card",
//            summary = "Update a card",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Successfully updated card"),
//                    @ApiResponse(responseCode = "404", description = "Card or user not found"),
//                    @ApiResponse(responseCode = "401", description = "Unauthorized to update card"),
//                    @ApiResponse(responseCode = "409", description = "Duplicate entity conflict")
//            }
//    )
//    public Card updateCardDetails(@PathVariable int userId, @PathVariable int cardId,
//                                  @Valid @RequestBody ExistingCardDto existingCardDto) {
//        return cardService.update(existingCardDto, cardId, userId);
//    }
//
//    @DeleteMapping("/{cardId}")
//    @Operation(
//            description = "Delete a card by ID",
//            summary = "Delete a card",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Successfully deleted card"),
//                    @ApiResponse(responseCode = "404", description = "Card or user not found"),
//                    @ApiResponse(responseCode = "401", description = "Unauthorized to delete card")
//            }
//    )
//    public Card deleteCard(@PathVariable UUID cardId){
//        return cardService.delete(cardId, user.getUsername());
//    }

}
