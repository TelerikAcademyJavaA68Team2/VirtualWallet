package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.card.CardEdit;
import com.example.virtualwallet.models.dtos.card.CardInput;
import com.example.virtualwallet.models.dtos.card.CardOutputForListMVC;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String getAllCardsByUser(Model model) {
        User user = userService.getAuthenticatedUser();
        List<CardOutputForListMVC> cardList = cardService.getAllCardsOutputForListMVCByUser(user.getId());
        model.addAttribute("cards", cardList);
        return "Cards-View";
    }

    @GetMapping("/new")
    public String showAddCardPage(Model model){

        model.addAttribute("action", "add");
        model.addAttribute("card", new CardInput());
        return "Card-Create-View";
    }

    @PostMapping("/new")
    public String addCard(@Valid @ModelAttribute("card") CardInput cardInput,
                          BindingResult errors, Model model) {

        model.addAttribute("action", "create");

        if(errors.hasErrors()){
            return "Card-Create-View";
        }

        cardService.addCard(cardInput);

        return "redirect:/mvc/profile/cards";
    }

    @GetMapping("/{id}/edit")
    public String getEditCardDetailsPage(Model model,
                                         @PathVariable(value = "id") UUID cardId) {
        Card card = cardService.getCardById(cardId);
        CardEdit cardEdit = ModelMapper.cardEditFromCard(card);
        if (!card.getOwner().equals(userService.getAuthenticatedUser())) {
            return "redirect:/mvc/home";
        }
        model.addAttribute("cardId", card.getId());
        model.addAttribute("card", card);
        model.addAttribute("cardDto", cardEdit);
        return "Card-Edit-View";
    }

    @PostMapping("/{id}/edit")
    public String editCardDetails(Model model,
                                  @PathVariable("id") UUID cardId,
                                  @Valid @ModelAttribute("cardDto") CardEdit existingCardDto,
                                  BindingResult result) {
        if (result.hasErrors()) {
            Card card = cardService.getCardById(cardId);
            model.addAttribute("cardId", card.getId());
            model.addAttribute("card", card);
            model.addAttribute("cardDto", existingCardDto);

            return "Card-Edit-View";
        }

        try {
            cardService.updateCard(existingCardDto, cardId);
        } catch (EntityNotFoundException | DuplicateEntityException e) {
            Card card = cardService.getCardById(cardId);
            model.addAttribute("cardId", card.getId());
            model.addAttribute("card", card);
            model.addAttribute("cardDto", existingCardDto);
            result.rejectValue("cardNumber", "cardNumber-duplicate", e.getMessage());
            return "Card-Edit-View";
        } catch (InvalidUserInputException e) {
            Card card = cardService.getCardById(cardId);
            model.addAttribute("cardId", card.getId());
            model.addAttribute("card", card);
            model.addAttribute("cardDto", existingCardDto);
            result.rejectValue("expirationDate", "expirationDate-expired", e.getMessage());
            return "Card-Edit-View";
        }

        return "redirect:/mvc/profile/cards";
    }

    @PostMapping("/{id}/delete")
    public String deleteCard(Model model,
                             @PathVariable("id") UUID cardId) {
        try {
            cardService.softDeleteCard(cardId);
        } catch (InvalidUserInputException | UnauthorizedAccessException e) {
            model.addAttribute("error", e.getMessage());
            getAllCardsByUser(model);
        }
        return "redirect:/mvc/profile/cards";
    }

}
