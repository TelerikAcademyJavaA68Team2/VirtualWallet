package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.card.*;
import com.example.virtualwallet.models.dtos.exchange.ExchangeOutput;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateOutput;
import com.example.virtualwallet.models.dtos.transactions.FullTransactionInfoOutput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;
import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserOutput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.dtos.wallet.ActivityOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ModelHelpers.maskCreditCard;

public class ModelMapper {

    public static Card createCardFromCardInput(CardInput cardInput, User user) {
        Card card = new Card();
        card.setCreatedAt(LocalDateTime.now());
        card.setCardNumber(cardInput.getCardNumber());
        card.setCardHolder(cardInput.getCardHolder());
        card.setCvv(cardInput.getCvv());
        card.setExpirationDate(cardInput.getExpirationDateAsYearMonth());
        card.setOwner(user);
        return card;
    }

    public static Card modifySoftDeletedCardFromCardInput(Card card, CardInput cardInput) {
        card.setCardHolder(cardInput.getCardHolder());
        card.setCvv(cardInput.getCvv());
        card.setExpirationDate(cardInput.getExpirationDateAsYearMonth());
        return card;
    }

    public static CardOutputForList displayForListCardOutputFromCreditCard(Card card) {
        CardOutputForList cardOutputForList = new CardOutputForList();
        cardOutputForList.setCardId(card.getId());
        cardOutputForList.setCardNumber(maskCreditCard(card.getCardNumber()));
        cardOutputForList.setExpirationDate(card.getExpirationDate());
        return cardOutputForList;
    }

    public static CardOutputForListMVC displayForListCardOutputMVCFromCreditCard(Card card) {
        CardOutputForListMVC cardOutputForList = new CardOutputForListMVC();
        cardOutputForList.setCardId(card.getId());
        cardOutputForList.setCardNumber(card.getCardNumber());
        cardOutputForList.setExpirationDate(card.getExpirationDate());
        cardOutputForList.setCardHolder(card.getCardHolder());
        cardOutputForList.setCvv(card.getCvv());
        return cardOutputForList;
    }

    public static CardOutput cardOutputFromCard(Card card) {
        CardOutput cardOutput = new CardOutput();
        cardOutput.setCardId(card.getId());
        cardOutput.setCardNumber(card.getCardNumber());
        cardOutput.setCardHolder(card.getCardHolder());
        cardOutput.setExpirationDate(card.getExpirationDate());
        cardOutput.setCvv(card.getCvv());
        return cardOutput;
    }

    public static UserProfileOutput userProfileFromUser(User user) {
        return new UserProfileOutput(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPhoto(),
                user.getRole().toString(),
                user.getStatus().toString(),
                user.getCreatedAt().toString().substring(0, 10));
    }

    public static UserOutput userOutputFromUser(User user) {
        return new UserOutput(
                user.getId(),
                user.getPhoto(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt());
    }

    public static Card updateCardFromCardEdit(CardEdit cardEdit, Card card) {
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

    public static TransferOutput transferToTransferOutput(Transfer transfer) {
        TransferOutput transferOutput = new TransferOutput();
        transferOutput.setTransferId(transfer.getId());
        transferOutput.setDate(transfer.getDate());
        transferOutput.setStatus(String.valueOf(transfer.getStatus()));
        transferOutput.setAmount(transfer.getAmount());
        transferOutput.setCurrency(String.valueOf(transfer.getCurrency()));
        transferOutput.setRecipientUsername(transfer.getRecipientUsername());
        return transferOutput;
    }

    public static FullTransferInfoOutput transferToFullTransferInfoOutput(Transfer transfer) {
        FullTransferInfoOutput transferOutput = new FullTransferInfoOutput();
        transferOutput.setTransferId(transfer.getId());
        transferOutput.setDate(transfer.getDate());
        transferOutput.setStatus(String.valueOf(transfer.getStatus()));
        transferOutput.setAmount(transfer.getAmount());
        transferOutput.setCurrency(String.valueOf(transfer.getCurrency()));
        transferOutput.setRecipientUsername(transfer.getRecipientUsername());
        transferOutput.setCardNumbers(maskCreditCard(transfer.getCard().getCardNumber()));

        return transferOutput;
    }

    public static TransactionOutput transactionToTransactionOutput(Transaction transaction) {
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionOutput.setTransactionId(transaction.getId());
        transactionOutput.setDate(transaction.getDate());
        transactionOutput.setCurrency(String.valueOf(transaction.getCurrency()));
        transactionOutput.setAmount(transaction.getAmount());
        transactionOutput.setSenderUsername(transaction.getSenderUsername());
        transactionOutput.setRecipientUsername(transaction.getRecipientUsername());
        return transactionOutput;
    }

    public static FullTransactionInfoOutput transactionToFullTransactionInfoOutput(Transaction transaction) {
        FullTransactionInfoOutput transactionOutput = new FullTransactionInfoOutput();
        transactionOutput.setTransactionId(transaction.getId());
        transactionOutput.setDate(transaction.getDate());
        transactionOutput.setCurrency(String.valueOf(transaction.getCurrency()));
        transactionOutput.setAmount(transaction.getAmount());
        transactionOutput.setSenderUsername(transaction.getSenderUsername());
        transactionOutput.setRecipientUsername(transaction.getRecipientUsername());
        transactionOutput.setDescription(transaction.getDescription());
        return transactionOutput;
    }

    public static ExchangeRateOutput exchangeRateOutputFromExchangeRate(ExchangeRate input) {
        ExchangeRateOutput output = new ExchangeRateOutput();
        output.setFromCurrency(input.getFromCurrency().name());
        output.setToCurrency(input.getToCurrency().name());
        output.setRate(input.getRate());
        return output;
    }

    public static ExchangeOutput exchangeToExchangeOutput(Exchange input) {
        ExchangeOutput output = new ExchangeOutput();
        output.setId(input.getId());
        output.setAmount(input.getAmount());
        output.setToAmount(input.getToAmount());
        output.setFromCurrency(input.getFromCurrency().name());
        output.setToCurrency(input.getToCurrency().name());
        output.setRecipientUsername(input.getRecipientUsername());
        output.setDate(input.getDate());
        return output;
    }

    public static FullExchangeInfoOutput exchangeToFullExchangeOutput(Exchange input) {
        FullExchangeInfoOutput output = new FullExchangeInfoOutput();
        output.setId(input.getId());
        output.setAmount(input.getAmount());
        output.setToAmount(input.getToAmount());
        output.setFromCurrency(input.getFromCurrency().name());
        output.setToCurrency(input.getToCurrency().name());
        output.setRecipientUsername(input.getRecipientUsername());
        output.setDate(input.getDate());
        output.setExchangeRate(input.getExchangeRate());
        return output;
    }

    public static Sort convertToSort(String sortBy, String sortOrder) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(sortDirection, sortBy);
    }

    public static ActivityOutput mapObjectToActivity(Object[] row) {
        return new ActivityOutput(
                (UUID) row[0],
                (String) row[1],
                (BigDecimal) row[2],
                (BigDecimal) row[3],
                (String) row[4],
                (String) row[5],
                (String) row[6],
                (String) row[7],
                (String) row[8],
                (String) row[9],
                (String) row[10],
                (String) row[11],
                row[12] != null ? ((Timestamp) row[12]).toLocalDateTime() : null

        );
    }

    public static WalletBasicOutput mapWalletToBasicWalletOutput(Wallet input) {
        WalletBasicOutput output = new WalletBasicOutput();
        output.setWalletId(input.getId());
        output.setBalance(input.getBalance());
        output.setCurrency(input.getCurrency().name());
        return output;
    }

    public static ProfileUpdateInput userOutputToUserUpdateInput(UserProfileOutput user) {
        ProfileUpdateInput dto = new ProfileUpdateInput();

        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }

    public static CardEdit cardEditFromCard(Card card) {
        CardEdit cardEdit = new CardEdit();
        cardEdit.setCardHolder(card.getCardHolder());
        cardEdit.setCardNumber(card.getCardNumber());
        cardEdit.setCvv(card.getCvv());
        cardEdit.setExpirationDate(card.getExpirationDate().format(DateTimeFormatter.ofPattern("MM/yy")));
        cardEdit.setCardId(card.getId());
        return cardEdit;
    }
}