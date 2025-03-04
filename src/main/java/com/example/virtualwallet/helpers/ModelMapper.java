package com.example.virtualwallet.helpers;


import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
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

    public Card modifySoftDeletedCardFromCardInput(Card card, CardInput cardInput){
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
}