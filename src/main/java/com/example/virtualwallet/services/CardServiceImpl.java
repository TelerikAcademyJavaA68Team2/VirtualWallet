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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.validateUserIsCardOwner;

@Service
@PropertySource("classpath:messages.properties")
public class CardServiceImpl implements CardService {

    @Value("${error.registeredAnotherUser}")
    public static String REGISTERED_TO_ANOTHER_USER;

    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, ModelMapper modelMapper, UserService userService) {
        this.cardRepository = cardRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

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
            if (!existingCard.getOwner().equals(user)) {
                throw new DuplicateEntityException(REGISTERED_TO_ANOTHER_USER);
            }
            if (existingCard.isDeleted()) {
                return restoreSoftDeletedCard(existingCard, user, cardDto);
            } else {
                throwIfCardWithSameNumberAlreadyExistsInSystem(cardDto.getCardNumber());
            }
        }

        return createAndSaveCard(cardDto, user);

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
