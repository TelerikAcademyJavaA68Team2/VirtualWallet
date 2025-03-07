package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.InsufficientFundsException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.TransactionInput;
import com.example.virtualwallet.models.dtos.TransactionOutput;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.TransactionRepository;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.validateAndConvertCurrency;
import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;

    @Override
    public TransactionOutput createTransaction(TransactionInput transactionInput) {
        User sender = userService.getAuthenticatedUser();

        if(sender.getStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Your account status is not active " +
                    "and you cannot make a transaction! Please, contact an Admin for further information.");
        }
        String recipientUsername = userService.findByUsernameOrEmailOrPhoneNumber(transactionInput.
                getUsernameOrEmailOrPhoneNumber());

        if (sender.getUsername().equals(recipientUsername)) {
            throw new InvalidUserInputException("Cannot send money to yourself!");
        }

        Currency transactionCurrency = validateAndConvertCurrency(transactionInput.getCurrency());

        BigDecimal amountToSend = transactionInput.getAmount();

        if(sender.getWallets().stream().noneMatch(wallet -> wallet.getCurrency().equals(transactionCurrency))){
            throw new InvalidUserInputException("You must first create a wallet with that currency " +
                    "and have enough balance to make a transaction!");
        }

        Wallet senderWallet = walletService.getOrCreateWalletByUsernameAndCurrency(sender.getUsername(),
                transactionCurrency);

        BigDecimal senderWalletBalance = senderWallet.getBalance();

        if (senderWalletBalance.compareTo(amountToSend) < 0) {
            throw new InsufficientFundsException(format("You don't have enough funds in your wallet to send to %s",
                    recipientUsername));
        }

        Wallet recipientWallet = walletService.getOrCreateWalletByUsernameAndCurrency(recipientUsername,
                transactionCurrency);

        BigDecimal recipientWalletBalance = recipientWallet.getBalance();

        senderWallet.setBalance(senderWalletBalance.subtract(amountToSend));

        recipientWallet.setBalance(recipientWalletBalance.add(amountToSend));

        Transaction transaction = ModelMapper.createTransactionFromTransactionInput(transactionInput,
                transactionCurrency, senderWallet, recipientWallet);

        transactionRepository.save(transaction);
        return ModelMapper.transactionToTransactionOutput(transaction);
    }

    @Override
    public List<TransactionOutput> findAllTransactionsByUserId(UUID userId) {
        return transactionRepository.findAllTransactionsBySenderWallet_Owner_IdOrRecipientWallet_Owner_Id
                        (userId, userId, Sort.by(Sort.Direction.DESC, "date"))
                .stream()
                .map(ModelMapper::transactionToTransactionOutput)
                .toList();
    }

    @Override
    public Set<Transaction> findAllTransactionsByWalletId(UUID walletId) {
        return null/* transactionRepository.findAllTransactionsByWalletId(walletId)*/;
    }
}