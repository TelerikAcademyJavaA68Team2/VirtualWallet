package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.dtos.card.*;

import java.util.List;
import java.util.UUID;

public interface CardService {

    Card getCardById(UUID id);

    CardOutput getCardOutputById(UUID id);

    List<Card> getAllCardsByUser(UUID userId);

    Integer getTotalNumberOfCardsByOwner_Id(UUID userId);

    List<CardOutputForList> getAllCardsOutputForListByUser(UUID userId);

    List<CardOutputForListMVC> getAllCardsOutputForListMVCByUser(UUID userId);

    CardOutput addCard(CardInput cardInput);

    CardOutput updateCard(CardEdit existingCardDto, UUID cardId);

    void softDeleteCard(UUID id);

}
