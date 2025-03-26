package com.example.virtualwallet;

import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.card.*;
import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransferStatus;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.virtualwallet.services.UserServiceImpl.DEFAULT_PROFILE_PIC_PNG;

public class Helpers {

    public static final String MOCK_USER_FIRST_NAME = "MockFirstNameUser";
    public static final String MOCK_USER_LAST_NAME = "MockLastNameUser";
    public static final String MOCK_CARD_CARDHOLDER = "Mock Holder";
    public static final String MOCK_CARD_CARDHOLDER_EDIT = "Mock Holder Edited";
    public static final String MOCK_CARD_CARD_NUMBER = "1234567891234567";
    public static final String MOCK_CARD_CARD_NUMBER_ANOTHER = "9234567891234567";
    public static final String MOCK_USER_USERNAME = "MockUsername";
    public static final String MOCK_ADMIN_USERNAME = "MockAdminName";
    public static final String MOCK_PASSWORD = "MockPassword";
    public static final String MOCK_USER_EMAIL = "mock@user.com";
    public static final String MOCK_ADMIN_EMAIL = "mock@admin.com";
    public static final String MOCK_CVV = "123";
    public static final YearMonth MOCK_YEAR_MONTH_EXPIRATION_DATE = YearMonth.of(2050, 7);
    public static final String MOCK_STRING_EXPIRATION_DATE = "07/25";
    public static final String USD = "USD";
    public static final String DATE = "date";
    public static final String SORT_ORDER = "asc";
    public static final String BIG_DECIMAL = "50.42";
    public static final String DESCRIPTION = "Test description";

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
        mockUser.setPhoto(DEFAULT_PROFILE_PIC_PNG);
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

    public static Card createMockExpiredCard(User owner) {
        Card mockCard = createMockCard(owner);
        mockCard.setExpirationDate(YearMonth.now().minusMonths(1));
        return mockCard;
    }

    public static TransferInput createMockTransferInput(UUID cardId, String amount) {
        TransferInput input = new TransferInput();
        input.setCardId(cardId);
        input.setAmount(new BigDecimal(amount));
        input.setCurrency(USD);
        return input;
    }

    public static TransactionFilterOptions createMockTransactionFilterOptions() {
        TransactionFilterOptions filters = new TransactionFilterOptions();
        filters.setPage(0);
        filters.setSize(5);
        filters.setSortBy(DATE);
        filters.setSortOrder(SORT_ORDER);
        return filters;
    }

    public static TransactionInput createMockTransactionInput() {
        return new TransactionInput(MOCK_USER_EMAIL,
                new BigDecimal(BIG_DECIMAL), USD, DESCRIPTION);
    }

    public static Exchange createMockExchange(User user) {
        Exchange exchange = new Exchange();
        exchange.setId(UUID.randomUUID());
        exchange.setExchangeRate(BigDecimal.ONE);
        exchange.setAmount(BigDecimal.TEN);
        exchange.setToAmount(BigDecimal.TEN);
        exchange.setFromCurrency(Currency.USD);
        exchange.setToCurrency(Currency.EUR);
        exchange.setRecipientUsername(user.getUsername());
        exchange.setFromWallet(Helpers.createMockWallet(user));
        exchange.setToWallet(Helpers.createMockWallet(user));
        return exchange;
    }


}
