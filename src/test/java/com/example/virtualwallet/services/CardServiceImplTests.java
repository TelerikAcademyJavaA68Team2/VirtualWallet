package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.CardEdit;
import com.example.virtualwallet.models.dtos.CardInput;
import com.example.virtualwallet.models.dtos.CardOutput;
import com.example.virtualwallet.models.dtos.CardOutputForList;
import com.example.virtualwallet.repositories.CardRepository;
import com.example.virtualwallet.services.contracts.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static com.example.virtualwallet.Helpers.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTests {

    public static final String REGISTERED_TO_ANOTHER_USER = "This card is already registered to another user!";

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    CardServiceImpl cardService;


    @Test
    public void getCardById_ShouldReturnCard_When_ValidArgs() {

        User user = createMockUserWithoutCardsAndWallets();
        Card mockCard = createMockCard(user);
        when(cardRepository.findById(any())).thenReturn(Optional.of(mockCard));

        Card result = cardService.getCardById(mockCard.getId());

        assertNotNull(result);
        Assertions.assertEquals(mockCard, result);
        verify(cardRepository, times(1)).findById(mockCard.getId());

    }

    @Test
    public void getCardOutputById_ShouldReturnCardOutput_When_ValidArgs() {

        Card mockCard = createMockCard(createMockUserWithCardsAndWallets());
        CardOutput mockCardOutput = createMockCardOutput();
        mockCardOutput.setCardId(mockCard.getId());

        when(cardRepository.findById(eq(mockCard.getId()))).thenReturn(Optional.of(mockCard));
        when(modelMapper.cardOutputFromCard(mockCard)).thenReturn(mockCardOutput);

        // Act
        CardOutput result = cardService.getCardOutputById(mockCard.getId());

        assertNotNull(result);
        Assertions.assertEquals(mockCardOutput, result);
        verify(cardRepository, times(1)).findById(mockCardOutput.getCardId());
        verify(modelMapper, times(1)).cardOutputFromCard(mockCard);

    }

    @Test
    public void getById_ShouldThrow_Exception_When_CardNotFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> cardService.getCardById(UUID.randomUUID()));

    }

    @Test
    public void getAllCards_Should_ReturnEmptyList() {
        // Arrange
        User user = createMockUserWithoutCardsAndWallets();
        user.setCards(new HashSet<>());
        when(cardRepository.getAllByOwner_Id(user.getId()))
                .thenReturn(new ArrayList<>());

        // Act
        List<Card> cards = cardService.getAllCardsByUser(user.getId());

        // Assert
        verify(cardRepository, Mockito.times(1))
                .getAllByOwner_Id(user.getId());

        Assertions.assertEquals(user.getCards().isEmpty(), cards.isEmpty());
    }

    @Test
    public void getAllCards_Should_CallRepository() {
        // Act
        cardService.getAllCardsByUser(new UUID(1, 1));

        // Assert
        verify(cardRepository, Mockito.times(1))
                .getAllByOwner_Id(new UUID(1, 1));
    }

    @Test
    public void getAllCards_Should_ReturnUserCards() {
        // Arrange
        User user = createMockUserWithCardsAndWallets();
        when(cardRepository.getAllByOwner_Id(user.getId()))
                .thenReturn(user.getCards().stream().toList());

        // Act
        List<Card> cards = cardService.getAllCardsByUser(user.getId());

        // Assert
        verify(cardRepository, Mockito.times(1))
                .getAllByOwner_Id(user.getId());

        Assertions.assertEquals(user.getCards().stream().toList(), cards);
    }

    @Test
    public void getTotalNumberOfCardsByOwnerId_Should_ReturnCorrectCount() {
        // Arrange
        UUID userId = UUID.randomUUID();
        int expectedCardCount = 5;

        when(cardRepository.getTotalNumberOfCardsByOwner_Id(userId)).thenReturn(expectedCardCount);

        // Act
        int actualCardCount = cardService.getTotalNumberOfCardsByOwner_Id(userId);

        // Assert
        verify(cardRepository, times(1)).getTotalNumberOfCardsByOwner_Id(userId);
        assertEquals(expectedCardCount, actualCardCount);
    }

    @Test
    public void getAllCardsOutputForList_Should_ReturnUserCardsOutPutForList() {
        // Arrange
        User user = createMockUserWithoutCardsAndWallets();
        Card mockCard = createMockCard(user);
        user.setCards(new HashSet<>(List.of(mockCard)));

        CardOutputForList mockCardOutputForList = createMockCardOutputForList();
        mockCardOutputForList.setCardId(mockCard.getId());

        when(cardRepository.getAllByOwner_Id(user.getId()))
                .thenReturn(user.getCards().stream().toList());
        when(modelMapper.displayForListCardOutputFromCreditCard(mockCard)).thenReturn(mockCardOutputForList);

        List<CardOutputForList> expectedCardsOutputForList = List.of(mockCardOutputForList);

        // Act
        List<CardOutputForList> cards = cardService.getAllCardsOutputForListByUser(user.getId());

        // Assert
        assertNotNull(cards);
        assertEquals(1, cards.size());
        Assertions.assertEquals(expectedCardsOutputForList, cards);
        assertEquals(mockCardOutputForList, cards.get(0));

        verify(cardRepository, Mockito.times(1))
                .getAllByOwner_Id(user.getId());

        verify(modelMapper, times(1)).displayForListCardOutputFromCreditCard(mockCard);
    }

    @Test
    public void addCard_Should_AddCard_WhenValidArgsAndNoCardExists() {
        CardInput cardInput = createMockCardInput();
        User user = createMockUserWithoutCardsAndWallets();
        Card card = createMockCard(user);
        CardOutput cardOutput = createMockCardOutput();
        cardOutput.setCardId(card.getId());

        when(userService.getAuthenticatedUser())
                .thenReturn(user);
        when(cardRepository.getCardByCardNumber(card.getCardNumber())).thenReturn(null);
        when(modelMapper.createCardFromCardInput(cardInput, user)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(modelMapper.cardOutputFromCard(card)).thenReturn(cardOutput);

        CardOutput cardOutputResult = cardService.addCard(cardInput);

        verify(cardRepository, Mockito.times(1)).save(card);
        verify(cardRepository, Mockito.times(1)).getCardByCardNumber(card.getCardNumber());

        assertEquals(cardOutputResult, cardOutput);
        assertNotNull(cardOutputResult);
    }

    @Test
    public void addCard_Should_MakeCardActive_WhenCardIsSoftDeleted() {
        CardInput cardInput = createMockCardInput();
        User user = createMockUserWithoutCardsAndWallets();
        Card card = createMockCard(user);
        card.markAsDeleted();
        CardOutput cardOutput = createMockCardOutput();
        cardOutput.setCardId(card.getId());

        when(userService.getAuthenticatedUser())
                .thenReturn(user);
        when(cardRepository.getCardByCardNumber(card.getCardNumber())).thenReturn(card);
        when(modelMapper.modifySoftDeletedCardFromCardInput(card, cardInput)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(modelMapper.cardOutputFromCard(card)).thenReturn(cardOutput);

        CardOutput cardOutputResult = cardService.addCard(cardInput);

        verify(cardRepository, Mockito.times(1)).save(card);
        verify(cardRepository, Mockito.times(1)).getCardByCardNumber(card.getCardNumber());

        assertEquals(cardOutputResult, cardOutput);
        assertFalse(card.isDeleted());
        assertNull(card.getDeletedAt());
        assertEquals(card.getOwner(), user);
        assertNotNull(cardOutputResult);
    }

    @Test
    public void addCard_Should_ThrowDuplicateEntityException_When_DifferentUserTriesToAddCardWithSameNumber() {
        CardInput cardInput = createMockCardInput();
        User user = createMockUserWithoutCardsAndWallets();
        User anotherUser = createMockUserWithoutCardsAndWallets();
        Card card = createMockCard(anotherUser);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardRepository.getCardByCardNumber(card.getCardNumber())).thenReturn(card);

        DuplicateEntityException duplicateEntityException = Assertions.assertThrows(DuplicateEntityException.class,
                () -> cardService.addCard(cardInput));

        verify(cardRepository, never()).save(any(Card.class));

        assertEquals(REGISTERED_TO_ANOTHER_USER, duplicateEntityException.getMessage());

    }

    @Test
    public void addCard_Should_ThrowDuplicateEntityException_When_TheSameUserTriesToAddExistingCard() {
        CardInput cardInput = createMockCardInput();
        User user = createMockUserWithoutCardsAndWallets();
        Card card = createMockCard(user);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardRepository.getCardByCardNumber(card.getCardNumber())).thenReturn(card);

        DuplicateEntityException duplicateEntityException = Assertions.assertThrows(DuplicateEntityException.class,
                () -> cardService.addCard(cardInput));

        verify(cardRepository, never()).save(any(Card.class));

        assertEquals(format("Card with number %s already exists.", card.getCardNumber()), duplicateEntityException.getMessage());

    }

    @Test
    public void updateCard_Should_UpdateCard_When_UserIsOwner() {
        CardEdit cardEdit = createMockCardEdit();
        User user = createMockUserWithoutCardsAndWallets();
        Card card = createMockCard(user);
        Card cardUpdated = createMockCard(user);
        cardUpdated.setCardHolder(cardEdit.getCardHolder());

        CardOutput cardOutput = createMockCardOutput();
        cardOutput.setCardHolder(cardEdit.getCardHolder());

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(modelMapper.updateCardFromCardInput(cardEdit, card)).thenReturn(cardUpdated);
        when(cardRepository.save(cardUpdated)).thenReturn(cardUpdated);
        when(modelMapper.cardOutputFromCard(cardUpdated)).thenReturn(cardOutput);

        CardOutput result = cardService.updateCard(cardEdit, card.getId());

        verify(cardRepository, Mockito.times(1)).findById(card.getId());
        verify(cardRepository, Mockito.times(1)).save(cardUpdated);
        assertEquals(result, cardOutput);

    }

    @Test
    public void updateCard_Should_ThrowUnauthorizedAccessException_When_IsNotOwner() {

        CardEdit cardEdit = createMockCardEdit();
        cardEdit.setCardNumber(MOCK_CARD_CARD_NUMBER_ANOTHER);

        User user = createMockUserWithoutCardsAndWallets();
        User anotherUser = createMockAdmin();

        Card card = createMockCard(user);
        card.setCardNumber(MOCK_CARD_CARD_NUMBER_ANOTHER);

        Card anotherCard = createMockCard(anotherUser);
        anotherCard.setCardNumber(MOCK_CARD_CARD_NUMBER_ANOTHER);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardRepository.findById(any())).thenReturn(Optional.of(anotherCard));

        UnauthorizedAccessException unauthorizedAccessException = Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> cardService.updateCard(cardEdit, card.getId()));

        verify(cardRepository, never()).save(any(Card.class));
        assertEquals("Only the card's owner can modify cards!", unauthorizedAccessException.getMessage());

    }

    @Test
    public void updateCard_Should_DuplicateEntityException_When_CardNumberAlreadyExists() {

        CardEdit cardEdit = createMockCardEdit();
        cardEdit.setCardNumber(MOCK_CARD_CARD_NUMBER_ANOTHER);

        User user = createMockUserWithoutCardsAndWallets();

        Card card = createMockCard(user);

        Card anotherCard = createMockCard(user);
        anotherCard.setCardNumber(MOCK_CARD_CARD_NUMBER_ANOTHER);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(cardRepository.getCardByCardNumber(cardEdit.getCardNumber())).thenReturn(anotherCard);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> cardService.updateCard(cardEdit, card.getId()));

        verify(cardRepository, never()).save(any(Card.class));

    }


    @Test
    public void deleteCard_Should_SoftDeleteCardById() {
        User user = createMockUserWithoutCardsAndWallets();
        Card card = createMockCard(user);


        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);

        cardService.softDeleteCard(card.getId());

        assertSame(card.getOwner(), user);
        verify(cardRepository, Mockito.times(1)).save(card);
        assertTrue(card.isDeleted());
    }

    @Test
    public void deleteCard_ShouldThrow_UnauthorizedAccessException_When_UserIsNotOwner() {
        User user = createMockUserWithoutCardsAndWallets();
        User anotherUser = createMockUserWithoutCardsAndWallets();
        Card card = createMockCard(anotherUser);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(cardRepository.findById(any())).thenReturn(Optional.of(card));

        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> cardService.softDeleteCard(card.getId()));

        assertNotSame(card.getOwner(), user);
        verify(cardRepository, never()).save(any(Card.class));
        assertFalse(card.isDeleted());
    }


}
