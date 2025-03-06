package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.CardEdit;
import com.example.virtualwallet.models.dtos.CardInput;
import com.example.virtualwallet.models.dtos.CardOutput;
import com.example.virtualwallet.models.dtos.CardOutputForList;
import com.example.virtualwallet.repositories.CardRepository;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.validateUserIsCardOwner;

@Service
@PropertySource("classpath:messages.properties")
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final Environment env;

    @Override
    public CardOutput getCardOutputById(UUID cardId) {
        Card card = findCardById(cardId);
        return modelMapper.cardOutputFromCard(card);
    }

    @Override
    public Card getCardById(UUID cardId) {
        return findCardById(cardId);
    }

    @Override
    public List<Card> getAllCardsByUser(UUID userId) {
        return findAllCardsByUser(userId);
    }

    @Override
    public Integer getTotalNumberOfCardsByOwner_Id(UUID userId) {
        return cardRepository.getTotalNumberOfCardsByOwner_Id(userId);
    }

    public List<CardOutputForList> getAllCardsOutputForListByUser(UUID userId) {
        return findAllCardsByUser(userId).stream()
                .map(modelMapper::displayForListCardOutputFromCreditCard)
                .toList();
    }

    @Override
    public CardOutput addCard(CardInput cardDto) {
        User user = userService.getAuthenticatedUser();
        Card existingCard = cardRepository.getCardByCardNumber(cardDto.getCardNumber());

        if (existingCard != null) {
            throwIfCardIsAlreadyAssignedToAnotherUser(existingCard, user);
            if (existingCard.isDeleted()) {
                return restoreSoftDeletedCard(existingCard, user, cardDto);
            } else {
                throwIfCardWithSameNumberAlreadyExistsInSystem(cardDto.getCardNumber());
            }
        }

        return createAndSaveCard(cardDto, user);

    }

    private void throwIfCardIsAlreadyAssignedToAnotherUser(Card existingCard, User user) {
        if (!existingCard.getOwner().equals(user)) {
            throw new DuplicateEntityException(env.getProperty("error.registeredAnotherUser"));
        }
    }

    @Override
    public CardOutput updateCard(CardEdit cardEdit, UUID cardId) {
        User user = userService.getAuthenticatedUser();
        Card card = findCardById(cardId);
        validateUserIsCardOwner(card, user);
        if(!card.getCardNumber().equals(cardEdit.getCardNumber())) {
            throwIfCardWithSameNumberAlreadyExistsInSystem(cardEdit.getCardNumber());
        }
        card = modelMapper.updateCardFromCardInput(cardEdit, card);
        cardRepository.save(card);
        return modelMapper.cardOutputFromCard(card);
    }

    @Override
    public void softDeleteCard(UUID cardId) {
        User user = userService.getAuthenticatedUser();
        Card card = findCardById(cardId);
        validateUserIsCardOwner(card, user);
        card.markAsDeleted();
        cardRepository.save(card);
    }

    private CardOutput createAndSaveCard(CardInput cardDto, User user) {
        Card newCard = modelMapper.createCardFromCardInput(cardDto, user);
        cardRepository.save(newCard);
        return modelMapper.cardOutputFromCard(newCard);
    }

    private Card findCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card", cardId));
    }

    private CardOutput restoreSoftDeletedCard(Card card, User user, CardInput cardInput) {
        validateUserIsCardOwner(card, user);
        card.markAsRestored();
        card = modelMapper.modifySoftDeletedCardFromCardInput(card, cardInput);
        cardRepository.save(card);

        return modelMapper.cardOutputFromCard(card);
    }

    private void throwIfCardWithSameNumberAlreadyExistsInSystem(String cardNumber) {
        if (cardRepository.getCardByCardNumber(cardNumber) != null) {
            throw new DuplicateEntityException("Card", "number", cardNumber);
        }
    }

    private List<Card> findAllCardsByUser(UUID userId) {
        return cardRepository.getAllByOwner_Id(userId);
    }
}
