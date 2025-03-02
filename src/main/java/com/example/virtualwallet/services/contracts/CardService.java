package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Dtos.CardExisting;
import com.example.virtualwallet.models.Dtos.CardInput;
import com.example.virtualwallet.models.Dtos.CardOutput;
import com.example.virtualwallet.models.Dtos.CardOutputForList;

import java.util.List;
import java.util.UUID;

public interface CardService {

    Card getCardById(UUID id);

    CardOutput getCardOutputById(UUID id);

    List<Card> getAllCardsByUser(UUID userId);

    List<CardOutputForList> getAllCardsOutputForListByUser(UUID userId);

    CardOutput addCard(CardInput cardInput);

    Card update(CardExisting existingCardDto, UUID cardId, String ownerUsername);

    Card update(CardExisting existingCardDto, UUID cardId, UUID userId);

    Card delete(UUID id, String ownerUsername);

}
