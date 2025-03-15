package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
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
    public String showAddCardPage(Model model) {
        model.addAttribute("action", "create");
        model.addAttribute("cardDto", new CardInput());
        return "Card-Create-View";
    }

    @PostMapping("/new")
    public String addCard(@Valid @ModelAttribute("cardDto") CardInput cardInput,
                          BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("action", "create");
            return "Card-Create-View";
        }
        try {
            cardService.addCard(cardInput);
        } catch (DuplicateEntityException e) {
            model.addAttribute("cardDto", cardInput);
            model.addAttribute("action", "create");
            errors.rejectValue("cardNumber", "cardNumber-duplicate", e.getMessage());
            return "Card-Create-View";
        } catch (InvalidUserInputException e) {
            model.addAttribute("cardDto", cardInput);
            model.addAttribute("action", "create");
            errors.rejectValue("expirationDate", "expirationDate-expired", e.getMessage());
            return "Card-Create-View";
        }
        return "redirect:/mvc/profile/cards";
    }

    @GetMapping("/{id}/edit")
    public String getEditCardDetailsPage(Model model, @PathVariable UUID id) {
        Card card = cardService.getCardById(id);
        CardEdit cardEdit = ModelMapper.cardEditFromCard(card);
        model.addAttribute("action", "edit");
        model.addAttribute("cardId", id);
        model.addAttribute("cardDto", cardEdit);
        return "Card-Edit-View";
    }

    @PostMapping("/{id}/edit")
    public String editCardDetails(Model model,
                                  @PathVariable("id") UUID cardId,
                                  @Valid @ModelAttribute("cardDto") CardEdit existingCardDto,
                                  BindingResult errors) {
        if (errors.hasErrors()) {
            Card card = cardService.getCardById(cardId);
            model.addAttribute("cardId", card.getId());
            model.addAttribute("card", card);
            model.addAttribute("cardDto", existingCardDto);
            model.addAttribute("action", "edit");

            return "Card-Edit-View";
        }

        try {
            cardService.updateCard(existingCardDto, cardId);
        } catch (DuplicateEntityException e) {
            Card card = cardService.getCardById(cardId);
            model.addAttribute("cardId", card.getId());
            model.addAttribute("card", card);
            model.addAttribute("cardDto", existingCardDto);
            model.addAttribute("action", "edit");
            errors.rejectValue("cardNumber", "cardNumber-duplicate", e.getMessage());

            return "Card-Edit-View";
        } catch (InvalidUserInputException e) {
            Card card = cardService.getCardById(cardId);
            model.addAttribute("cardId", card.getId());
            model.addAttribute("card", card);
            model.addAttribute("cardDto", existingCardDto);
            model.addAttribute("action", "edit");
            errors.rejectValue("expirationDate", "expirationDate-expired", e.getMessage());

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
