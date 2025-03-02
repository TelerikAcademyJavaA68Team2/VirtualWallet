package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.dtos.CardExisting;
import com.example.virtualwallet.models.dtos.CardInput;
import com.example.virtualwallet.models.dtos.CardOutput;
import com.example.virtualwallet.models.dtos.CardOutputForList;
import com.example.virtualwallet.repositories.CardRepository;
import com.example.virtualwallet.services.contracts.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@PropertySource("classpath:messages.properties")
public class CardServiceImpl implements CardService {

    @Value("${error.cardNotFound}")
    public static String CARD_NOT_FOUND;

    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, ModelMapper modelMapper) {
        this.cardRepository = cardRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CardOutput getCardOutputById(UUID id) {
        try {
            return modelMapper.cardOutputFromCard(cardRepository.getCardById(id));
        }catch (jakarta.persistence.EntityNotFoundException e){
            throw new EntityNotFoundException(CARD_NOT_FOUND);
        }
    }

    @Override
    public Card getCardById(UUID id) {
        try {
            return cardRepository.getCardById(id);
        } catch (jakarta.persistence.EntityNotFoundException e){
            throw new EntityNotFoundException(CARD_NOT_FOUND);
        }
    }

    @Override
    public List<Card> getAllCardsByUser(UUID userId) {
        return cardRepository.getAllByOwner_Id(userId);
    }

    public List<CardOutputForList> getAllCardsOutputForListByUser(UUID userId) {
        return cardRepository.getAllByOwner_Id(userId).stream()
                .map(modelMapper::displayForListCardOutputFromCreditCard)
                .toList();
    }

    @Override
    public CardOutput addCard(CardInput cardDto) {
        return null;
    }

    @Override
    public Card update(CardExisting existingCardDto, UUID cardId, String ownerUsername) {
        return null;
    }

    @Override
    public Card update(CardExisting existingCardDto, UUID cardId, UUID userId) {
        return null;
    }

    @Override
    public Card delete(UUID id, String ownerUsername) {
        return null;
    }

//    @Override
//    public Card create(String cardOwnerUsername, CardInput cardDto) {
//        Card card = new Card();
//        throwIfCardWithSameNumberAlreadyExistsInSystem(card);
//        setNameFromCardNumber(paymentInstrument, card);
//        paymentInstrument = paymentInstrumentService.create(user, paymentInstrument);
//        card.setId(paymentInstrument.getId());
//        return cardRepository.create(card);
//    }

}
