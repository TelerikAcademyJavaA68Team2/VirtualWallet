package com.example.virtualwallet.helpers;


import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.*;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.example.virtualwallet.helpers.ModelHelpers.maskCreditCard;

@Component
public class ModelMapper {


    public Card createCardFromCardInput(CardInput cardInput, User user) {
        Card card = new Card();
        card.setCreatedAt(LocalDateTime.now());
        card.setCardNumber(cardInput.getCardNumber());
        card.setCardHolder(cardInput.getCardHolder());
        card.setCvv(cardInput.getCvv());
        card.setExpirationDate(cardInput.getExpirationDateAsYearMonth());
        card.setOwner(user);
        return card;
    }

    public Card modifySoftDeletedCardFromCardInput(Card card, CardInput cardInput){
        card.setCardHolder(cardInput.getCardHolder());
        card.setCvv(cardInput.getCvv());
        card.setExpirationDate(cardInput.getExpirationDateAsYearMonth());
        return card;
    }

    public CardOutputForList displayForListCardOutputFromCreditCard(Card card) {
        CardOutputForList cardOutputForList = new CardOutputForList();
        cardOutputForList.setCardId(card.getId());
        cardOutputForList.setCardNumber(maskCreditCard(card.getCardNumber()));
        cardOutputForList.setExpirationDate(card.getExpirationDate());
        return cardOutputForList;
    }

    public CardOutput cardOutputFromCard(Card card) {
        CardOutput cardOutput = new CardOutput();
        cardOutput.setCardId(card.getId());
        cardOutput.setCardNumber(card.getCardNumber());
        cardOutput.setCardHolder(card.getCardHolder());
        cardOutput.setExpirationDate(card.getExpirationDate());
        cardOutput.setCvv(card.getCvv());
        return cardOutput;
    }

    public UserProfileOutput userProfileFromUser(User user) {
        return new UserProfileOutput(user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt());
    }


    public Card updateCardFromCardInput(CardEdit cardEdit, Card card) {
        if (cardEdit.getCardNumber() != null) {
            card.setCardNumber(cardEdit.getCardNumber());
        }
        if (cardEdit.getCvv() != null) {
            card.setCvv(cardEdit.getCvv());
        }
        if (cardEdit.getCardHolder() != null) {
            card.setCardHolder(cardEdit.getCardHolder());
        }
        if (cardEdit.getExpirationDateAsYearMonth() != null) {
            card.setExpirationDate(cardEdit.getExpirationDateAsYearMonth());
        }
        return card;
    }
}