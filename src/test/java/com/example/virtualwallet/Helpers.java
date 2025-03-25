package com.example.virtualwallet;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.card.*;
import com.example.virtualwallet.models.dtos.user.UserOutput;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransferStatus;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Role;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Helpers {

    public static final String MOCK_USER_FIRST_NAME = "MockFirstNameUser";
    public static final String MOCK_ADMIN_FIRST_NAME = "MockFirstNameAdmin";
    public static final String MOCK_USER_LAST_NAME = "MockLastNameUser";
    public static final String MOCK_ADMIN_LAST_NAME = "MockLastNameAdmin";
    public static final String MOCK_CARD_CARDHOLDER = "Mock Holder";
    public static final String MOCK_CARD_CARDHOLDER_EDIT = "Mock Holder Edited";
    public static final String MOCK_CARD_CARD_NUMBER = "1234567891234567";
    public static final String MOCK_CARD_CARD_NUMBER_ANOTHER = "9234567891234567";
    public static final String MOCK_USER_USERNAME = "MockUsername";
    public static final String MOCK_ADMIN_USERNAME = "MockAdminName";
    public static final String MOCK_PASSWORD = "MockPassword";
    public static final String MOCK_PHONE_NUMBER = "1231231234";
    public static final String MOCK_USER_EMAIL = "mock@user.com";
    public static final String MOCK_ADMIN_EMAIL = "mock@admin.com";
    public static final String MOCK_ACCOUNT_STATUS = "ACTIVE";
    public static final String MOCK_USER_ORDER_BY = "username";
    public static final String MOCK_CVV = "123";
    public static final YearMonth MOCK_YEAR_MONTH_EXPIRATION_DATE = YearMonth.of(2050, 7);
    public static final String MOCK_STRING_EXPIRATION_DATE = "07/25";

    public static User createMockUserWithCardsAndWallets() {
        User mockUser = createMockUserWithoutCardsAndWallets();

        Wallet mockWallet = createMockWallet(mockUser);
        Card mockCard = createMockCard(mockUser);
        Transfer mockTransfer = createMockTransfer(mockCard, mockWallet);

        mockCard.getTransfers().add(mockTransfer);
        mockUser.setWallets(Set.of(mockWallet));
        mockUser.setCards(Set.of(mockCard));

        return mockUser;
    }

    public static User createMockUserWithoutCardsAndWallets() {
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setFirstName(MOCK_USER_FIRST_NAME);
        mockUser.setLastName(MOCK_USER_LAST_NAME);
        mockUser.setEmail(MOCK_USER_EMAIL);
        mockUser.setUsername(MOCK_USER_USERNAME);
        mockUser.setPassword(MOCK_PASSWORD);
        mockUser.setRole(Role.USER);
        mockUser.setPhoto("/static/images/default-profile-pic.png");
        mockUser.setStatus(AccountStatus.ACTIVE);
        mockUser.setRole(Role.USER);
        mockUser.setCreatedAt(LocalDateTime.now());
        return mockUser;
    }

    public static Card createMockCard(User owner) {
        Card mockCard = new Card();
        mockCard.setId(UUID.randomUUID());
        mockCard.setCardHolder(MOCK_CARD_CARDHOLDER);
        mockCard.setCardNumber(MOCK_CARD_CARD_NUMBER);
        mockCard.setCvv(MOCK_CVV);
        mockCard.setExpirationDate(MOCK_YEAR_MONTH_EXPIRATION_DATE);
        mockCard.setCreatedAt(LocalDateTime.now());
        mockCard.setOwner(owner);
        mockCard.setTransfers(new HashSet<>());

        return mockCard;
    }

    public static Card createMockDeletedCard(User owner) {
        Card mockCard = new Card();
        mockCard.setId(UUID.randomUUID());
        mockCard.setCardHolder(MOCK_CARD_CARDHOLDER);
        mockCard.setCardNumber(MOCK_CARD_CARD_NUMBER);
        mockCard.setCvv(MOCK_CVV);
        mockCard.setExpirationDate(MOCK_YEAR_MONTH_EXPIRATION_DATE);
        mockCard.setCreatedAt(LocalDateTime.now());
        mockCard.setOwner(owner);
        mockCard.setTransfers(new HashSet<>());
        mockCard.markAsDeleted();

        return mockCard;
    }

    public static CardOutput createMockCardOutput() {
        CardOutput mockCardOutput = new CardOutput();
        mockCardOutput.setCardId(UUID.randomUUID());
        mockCardOutput.setCardHolder(MOCK_CARD_CARDHOLDER);
        mockCardOutput.setCardNumber(MOCK_CARD_CARD_NUMBER);
        mockCardOutput.setCvv(MOCK_CVV);
        mockCardOutput.setExpirationDate(MOCK_YEAR_MONTH_EXPIRATION_DATE);
        return mockCardOutput;
    }

    public static CardOutputForList createMockCardOutputForList() {
        CardOutputForList mockCardOutputForList = new CardOutputForList();
        mockCardOutputForList.setCardId(UUID.randomUUID());
        mockCardOutputForList.setCardNumber(MOCK_CARD_CARD_NUMBER);
        mockCardOutputForList.setExpirationDate(MOCK_YEAR_MONTH_EXPIRATION_DATE);
        return mockCardOutputForList;
    }

    public static CardOutputForListMVC createMockCardOutputForListMVC() {
        CardOutputForListMVC mockCardOutputForList = new CardOutputForListMVC();
        mockCardOutputForList.setCardId(UUID.randomUUID());
        mockCardOutputForList.setCardNumber(MOCK_CARD_CARD_NUMBER);
        mockCardOutputForList.setExpirationDate(MOCK_YEAR_MONTH_EXPIRATION_DATE);
        return mockCardOutputForList;
    }

    public static CardInput createMockCardInput() {
        CardInput cardInput = new CardInput();
        cardInput.setCardHolder(MOCK_CARD_CARDHOLDER);
        cardInput.setCardNumber(MOCK_CARD_CARD_NUMBER);
        cardInput.setCvv(MOCK_CVV);
        cardInput.setExpirationDate(MOCK_STRING_EXPIRATION_DATE);
        return cardInput;
    }

    public static CardEdit createMockCardEdit() {
        CardEdit cardEdit = new CardEdit();
        cardEdit.setCardHolder(MOCK_CARD_CARDHOLDER_EDIT);
        cardEdit.setCardNumber(MOCK_CARD_CARD_NUMBER);
        cardEdit.setCvv(MOCK_CVV);
        cardEdit.setExpirationDate(MOCK_STRING_EXPIRATION_DATE);
        return cardEdit;
    }

    public static Wallet createMockWallet(User owner) {
        Wallet mockWallet = new Wallet();
        mockWallet.setId(UUID.randomUUID());
        mockWallet.setCurrency(Currency.USD);
        mockWallet.setBalance(BigDecimal.valueOf(100.00));
        mockWallet.setCreatedAt(LocalDateTime.now());
        mockWallet.setOwner(owner);

        return mockWallet;
    }

    public static Transfer createMockTransfer(Card card, Wallet wallet) {
        Transfer mockTransfer = new Transfer();
        mockTransfer.setId(UUID.randomUUID());
        mockTransfer.setAmount(BigDecimal.valueOf(50.00));
        mockTransfer.setStatus(TransferStatus.APPROVED);
        mockTransfer.setDate(LocalDateTime.now());
        mockTransfer.setCard(card);
        mockTransfer.setWallet(wallet);

        return mockTransfer;
    }

    public static User createMockAdmin() {
        User mockAdmin = createMockUserWithCardsAndWallets();
        mockAdmin.setRole(Role.ADMIN);
        mockAdmin.setUsername(MOCK_ADMIN_USERNAME);
        mockAdmin.setEmail(MOCK_ADMIN_EMAIL);
        return mockAdmin;
    }

    public static UserOutput createMockUserOutput() {
        return new UserOutput(UUID.randomUUID(),
                null,
                MOCK_USER_USERNAME,
                MOCK_USER_EMAIL,
                MOCK_PHONE_NUMBER,
                Role.USER,
                AccountStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

//    public static UserFilterOptions createMockUserFilterOptions() {
//        return new UserFilterOptions(
//                MOCK_PHONE_NUMBER,
//                MOCK_USER_USERNAME,
//                MOCK_USER_EMAIL,
//                Role.USER.name(),
//                MOCK_ACCOUNT_STATUS,
//                0,
//                10,
//                MOCK_USER_ORDER_BY
//        );
//    }

    public static Pageable createMockPageable() {
        return PageRequest.of(0, 20, Sort.unsorted());
    }

    public static Page<Object[]> createMockPage() {
        return new PageImpl<>(Collections.singletonList(new Object[]{
                MOCK_USER_USERNAME,
                MOCK_USER_EMAIL,
                MOCK_PHONE_NUMBER,
                Role.USER.name(),
                true,
                5L
        }));
    }
}
