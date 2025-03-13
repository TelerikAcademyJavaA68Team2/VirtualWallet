package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.card.CardEdit;
import com.example.virtualwallet.models.dtos.card.CardOutputForListMVC;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/profile/cards")
public class CardMvcController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping
    public String getAllCardsByUser(Model model) {
        User user = userService.getAuthenticatedUser();
        List<CardOutputForListMVC> cardList = cardService.getAllCardsOutputForListMVCByUser(user.getId());
        model.addAttribute("cards", cardList);
        return "Cards-View";
    }

    @GetMapping("/{id}/edit")
    public String getEditCardDetailsPage(Model model,
                                         @PathVariable(value = "id") UUID cardId) {
        Card card = cardService.getCardById(cardId);
        if (!card.getOwner().equals(userService.getAuthenticatedUser())) {
            return "redirect:/mvc/home";
        }
        model.addAttribute("cardId", card.getId());
        model.addAttribute("card", card);
        model.addAttribute("cardDto", new CardEdit());
        return "Card-Edit-View";
    }

    @PostMapping("/{id}/delete")
    public String deleteCard(Model model,
                             @PathVariable("id") UUID cardId) {
        try {
            cardService.softDeleteCard(cardId);
        } catch (InvalidUserInputException e) {
            // model.setStatus(HttpStatus.BAD_REQUEST);
            model.addAttribute("error", e.getMessage());
            getAllCardsByUser(model);
        } catch (UnauthorizedAccessException e) {
           // model.setStatus(HttpStatus.FORBIDDEN);
            model.addAttribute("error", e.getMessage());
            getAllCardsByUser(model);
        }
        return "redirect:/mvc/profile/cards";
    }


    @PostMapping("/{id}/edit")
    public String editCardDetails(Model model,
                                        @PathVariable("id") UUID cardId,
                                        @Valid @ModelAttribute("cardDto") CardEdit existingCardDto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            //model.setStatus(HttpStatus.BAD_REQUEST);
            Card card = cardService.getCardById(cardId);
            model.addAttribute("cardId", card.getId());
            model.addAttribute("card", card);
            model.addAttribute("cardDto", existingCardDto);
            return "Card-Edit-View";
        } else {
            try {
                cardService.updateCard(existingCardDto, cardId);
            } catch (EntityNotFoundException e) {
              //  model.setStatus(HttpStatus.BAD_REQUEST);
                model.addAttribute("error", e.getMessage());
                getAllCardsByUser(model);
            } catch (DuplicateEntityException e) {
              //  model.setStatus(HttpStatus.CONFLICT);
                model.addAttribute("error", e.getMessage());
                getAllCardsByUser(model);
            }

        }
        return "redirect:/mvc/profile/cards";
    }

//    @PostMapping("/mvc/profile/cards/{cardId}/edit")
//    @ResponseBody
//    public ResponseEntity<?> editCard(@PathVariable UUID cardId, @RequestBody CardEdit request) {
//        try{
//            cardService.updateCard(request, cardId);
//            return ResponseEntity.ok(Map.of("message", "Card updated successfully!"));
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Card not found!"));
//        } catch (UnauthorizedAccessException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Not card owner!"));
//        } catch (DuplicateEntityException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Card number already exists!"));
//        }
//    }


}
