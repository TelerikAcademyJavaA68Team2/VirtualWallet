package com.example.virtualwallet.helpers;


import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.*;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.example.virtualwallet.helpers.ModelHelpers.maskCreditCard;

@Component
public class ModelMapper {


    public Card createCardFromCardInput(CardInput cardInput, User user) {
        Card card = new Card();
        card.setCreatedAt(LocalDateTime.now());
        card.setCardNumber(cardInput.getCardNumber());
        card.setCardHolder(cardInput.getCardHolder());
        card.setCvv(cardInput.getCvv());
        card.setExpirationDate(cardInput.getExpirationDateAsYearMonth());
        card.setOwner(user);
        return card;
    }

    public Card modifySoftDeletedCardFromCardInput(Card card, CardInput cardInput) {
        card.setCardHolder(cardInput.getCardHolder());
        card.setCvv(cardInput.getCvv());
        card.setExpirationDate(cardInput.getExpirationDateAsYearMonth());
        return card;
    }

    public CardOutputForList displayForListCardOutputFromCreditCard(Card card) {
        CardOutputForList cardOutputForList = new CardOutputForList();
        cardOutputForList.setCardId(card.getId());
        cardOutputForList.setCardNumber(maskCreditCard(card.getCardNumber()));
        cardOutputForList.setExpirationDate(card.getExpirationDate());
        return cardOutputForList;
    }

    public CardOutput cardOutputFromCard(Card card) {
        CardOutput cardOutput = new CardOutput();
        cardOutput.setCardId(card.getId());
        cardOutput.setCardNumber(card.getCardNumber());
        cardOutput.setCardHolder(card.getCardHolder());
        cardOutput.setExpirationDate(card.getExpirationDate());
        cardOutput.setCvv(card.getCvv());
        return cardOutput;
    }

    public UserProfileOutput userProfileFromUser(User user) {
        return new UserProfileOutput(user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt());
    }

    public Card updateCardFromCardInput(CardEdit cardEdit, Card card) {
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

    public TransferOutput transferToTransferOutput(Transfer transfer) {
        TransferOutput transferOutput = new TransferOutput();
        transferOutput.setTransferId(transfer.getId());
        transferOutput.setDate(transfer.getDate());
        transferOutput.setStatus(String.valueOf(transfer.getStatus()));
        transferOutput.setAmount(transfer.getAmount());
        transferOutput.setCurrency(String.valueOf(transfer.getCurrency()));
        transferOutput.setCardId(transfer.getCard().getId());
        transferOutput.setWalletId(transfer.getWallet().getId());
        return transferOutput;
    }

    public Transfer createTransferFromTransferInput(TransferInput transferInput, Card card, Wallet wallet,
                                                    TransactionStatus transferStatus, Currency currency) {
        Transfer transfer = new Transfer();
        transfer.setCard(card);
        transfer.setWallet(wallet);
        transfer.setAmount(transferInput.getAmount());
        transfer.setCurrency(currency);
        transfer.setStatus(transferStatus);
        return transfer;
    }

    public WalletBasicOutput mapWalletToBasicWalletOutput(Wallet input) {
        WalletBasicOutput output = new WalletBasicOutput();
        output.setWalletId(input.getId());
        output.setBalance(input.getBalance());
        output.setCurrency(input.getCurrency());
        return output;
    }

    public Transaction createTransactionFromTransactionInput(TransactionInput transactionInput,
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

    public TransactionOutput transactionToTransactionOutput(Transaction transaction) {
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionOutput.setTransactionId(transaction.getId());
        transactionOutput.setDate(transaction.getDate());
        transactionOutput.setCurrency(String.valueOf(transaction.getCurrency()));
        transactionOutput.setAmount(transaction.getAmount());
        transactionOutput.setSenderUsername(transaction.getSenderWallet().getOwner().getUsername());
        transactionOutput.setRecipientUsername(transaction.getRecipientWallet().getOwner().getUsername());
        return transactionOutput;
    }
}