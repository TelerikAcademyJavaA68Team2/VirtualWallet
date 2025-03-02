package com.example.virtualwallet.helpers;


import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Dtos.CardInput;
import com.example.virtualwallet.models.Dtos.CardOutput;
import com.example.virtualwallet.models.Dtos.CardOutputForList;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.example.virtualwallet.helpers.ModelHelpers.maskCreditCard;

@Component
public class ModelMapper {


//    public Page<UserOutput> mapObjectPageToUserOutputPage(Page<Object[]> usersObjectArray) {
//        return usersObjectArray.map(this::mapObjectToUserOutput);
//    }

    public Card createCardFromCardInput(CardInput cardInput, User user){
        Card card = new Card();
        card.setCreatedAt(LocalDateTime.now());
        card.setCardNumber(card.getCardNumber());
        card.setCardHolder(card.getCardHolder());
        card.setCvv(card.getCvv());
        card.setExpirationDate(card.getExpirationDate());
        card.setOwner(user);
        return card;
    }

    public CardOutputForList displayForListCardOutputFromCreditCard(Card card){
        CardOutputForList cardOutputForList = new CardOutputForList();
        cardOutputForList.setCardId(card.getId());
        cardOutputForList.setCardNumber(maskCreditCard(card.getCardNumber()));
        cardOutputForList.setExpirationDate(card.getExpirationDate());
        return cardOutputForList;
    }

    public CardOutput cardOutputFromCard(Card card){
        CardOutput cardOutput = new CardOutput();
        cardOutput.setCardId(card.getId());
        cardOutput.setCardNumber(card.getCardNumber());
        cardOutput.setCardHolder(card.getCardHolder());
        cardOutput.setExpirationDate(card.getExpirationDate());
        cardOutput.setCcv(card.getCvv());
        return cardOutput;
    }


}