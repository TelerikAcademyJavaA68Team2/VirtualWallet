package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.card.CardEdit;
import com.example.virtualwallet.models.dtos.card.CardInput;
import com.example.virtualwallet.models.dtos.card.CardOutput;
import com.example.virtualwallet.models.dtos.card.CardOutputForList;
import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateOutput;
import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;
import com.example.virtualwallet.models.dtos.user.UserOutput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;

import java.time.LocalDateTime;

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
        return new UserProfileOutput(user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt());
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

    public static Card updateCardFromCardInput(CardEdit cardEdit, Card card) {
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
        transferOutput.setCardNumber(maskCreditCard(transfer.getCard().getCardNumber()));
        return transferOutput;
    }

    public static TransferOutput transferToTransferOutput(Transfer transfer, String cardNumber, String currency) {
        TransferOutput transferOutput = new TransferOutput();
        transferOutput.setTransferId(transfer.getId());
        transferOutput.setDate(transfer.getDate());
        transferOutput.setStatus(String.valueOf(transfer.getStatus()));
        transferOutput.setAmount(transfer.getAmount());
        transferOutput.setCurrency(currency);
        transferOutput.setCardNumber(maskCreditCard(cardNumber));
        return transferOutput;
    }

    public static Transfer createTransferFromTransferInput(TransferInput transferInput, Card card, Wallet wallet,
                                                           TransactionStatus transferStatus, Currency currency) {
        Transfer transfer = new Transfer();
        transfer.setCard(card);
        transfer.setWallet(wallet);
        transfer.setAmount(transferInput.getAmount());
        transfer.setCurrency(currency);
        transfer.setStatus(transferStatus);
        return transfer;
    }

    public static WalletBasicOutput mapWalletToBasicWalletOutput(Wallet input) {
        WalletBasicOutput output = new WalletBasicOutput();
        output.setWalletId(input.getId());
        output.setBalance(input.getBalance());
        output.setCurrency(input.getCurrency());
        return output;
    }

    public static Transaction createTransactionFromTransactionInput(TransactionInput transactionInput,
                                                                    Currency currency,
                                                                    Wallet senderWallet,
                                                                    Wallet recipientWallet) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionInput.getAmount());
        transaction.setCurrency(currency);
        transaction.setSenderWallet(senderWallet);
        transaction.setRecipientWallet(recipientWallet);
        return transaction;
    }

    public static TransactionOutput transactionToTransactionOutput(Transaction transaction) {
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionOutput.setTransactionId(transaction.getId());
        transactionOutput.setDate(transaction.getDate());
        transactionOutput.setCurrency(String.valueOf(transaction.getCurrency()));
        transactionOutput.setAmount(transaction.getAmount());
        transactionOutput.setSenderUsername(transaction.getSenderWallet().getOwner().getUsername());
        transactionOutput.setRecipientUsername(transaction.getRecipientWallet().getOwner().getUsername());
        return transactionOutput;
    }

    public static TransactionOutput transactionToTransactionOutput(Transaction transaction,
                                                                   String sender, String recipient) {
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionOutput.setTransactionId(transaction.getId());
        transactionOutput.setDate(transaction.getDate());
        transactionOutput.setCurrency(String.valueOf(transaction.getCurrency()));
        transactionOutput.setAmount(transaction.getAmount());
        transactionOutput.setSenderUsername(sender);
        transactionOutput.setRecipientUsername(recipient);
        return transactionOutput;
    }

    public static ExchangeRateOutput exchangeRateOutputFromExchangeRate(ExchangeRate input) {
        ExchangeRateOutput output = new ExchangeRateOutput();
        output.setFromCurrency(input.getFromCurrency().name());
        output.setToCurrency(input.getToCurrency().name());
        output.setRate(input.getRate());
        return output;
    }
}