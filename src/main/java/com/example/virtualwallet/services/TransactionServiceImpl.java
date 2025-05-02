package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientFundsException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.transactions.FullTransactionInfoOutput;
import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import com.example.virtualwallet.repositories.TransactionRepository;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import com.example.virtualwallet.services.specifications.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ModelMapper.*;
import static com.example.virtualwallet.helpers.ValidationHelpers.validateAndConvertCurrency;
import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    public static final String CANNOT_SEND_MONEY_TO_YOURSELF = "Cannot send money to yourself!";
    public static final String NO_WALLET_WITH_CURRENCY = "You must first create a wallet with that currency " +
            "and have enough balance to make a transaction!";
    public static final String NOT_ENOUGH_FUNDS = "You don't have enough funds in your wallet to send to %s.";

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;

    @Override
    public FullTransactionInfoOutput getTransactionById(UUID id) {
        User user = userService.getAuthenticatedUser();

        Transaction transaction;
        if (user.getRole().equals(Role.ADMIN)) {
            transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Transaction", id));
        } else {
            transaction = transactionRepository.findByIdAndUsername(id, user.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("Transaction", id));
        }
        return transactionToFullTransactionInfoOutput(transaction);
    }

    @Override
    public TransactionOutput createTransaction(TransactionInput transactionInput) {
        User sender = userService.getAuthenticatedUser();

        User recipient = userService.findUserByUsernameOrEmailOrPhoneNumber(transactionInput.
                getUsernameOrEmailOrPhoneNumber());

        if (sender.getUsername().equals(recipient.getUsername())) {
            throw new InvalidUserInputException(CANNOT_SEND_MONEY_TO_YOURSELF);
        }

        Currency transactionCurrency = validateAndConvertCurrency(transactionInput.getCurrency());
        BigDecimal amountToSend = transactionInput.getAmount();

        if (!walletService.checkIfUserHasActiveWalletWithCurrency(sender.getId(), transactionCurrency)) {
            throw new InvalidUserInputException(NO_WALLET_WITH_CURRENCY);
        }

        Wallet senderWallet = walletService.getOrCreateWalletByUsernameAndCurrency(sender.getUsername(),
                transactionCurrency);

        BigDecimal senderWalletBalance = senderWallet.getBalance();
        if (senderWalletBalance.compareTo(amountToSend) < 0) {
            throw new InsufficientFundsException(format(NOT_ENOUGH_FUNDS,
                    recipient.getUsername()));
        }

        Wallet recipientWallet = walletService.getOrCreateWalletByUsernameAndCurrency(
                recipient.getUsername(), transactionCurrency
        );

        BigDecimal recipientWalletBalance = recipientWallet.getBalance();

        senderWallet.setBalance(senderWalletBalance.subtract(amountToSend));
        recipientWallet.setBalance(recipientWalletBalance.add(amountToSend));

        String description = (transactionInput.getDescription() == null || transactionInput.getDescription().isEmpty())
                ? "Transaction"
                : transactionInput.getDescription();

        Transaction transaction = ModelMapper.transactionInputToTransaction(
                sender, recipient, senderWallet, recipientWallet, amountToSend, description
        );

        transaction = transactionRepository.save(transaction);
        return transactionToTransactionOutput(transaction);
    }

    @Override
    public void createTransactionMVC(User sender, User recipient, Wallet senderWallet, BigDecimal amount, String description) {
        if (sender.getId().equals(recipient.getId())) {
            throw new InvalidUserInputException(CANNOT_SEND_MONEY_TO_YOURSELF);
        }

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(format(NOT_ENOUGH_FUNDS, recipient.getUsername()));
        }

        Wallet recipientWallet = walletService.getOrCreateWalletByUsernameAndCurrency
                (recipient.getUsername(), senderWallet.getCurrency());

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));

        String descriptionToSave = (description == null || description.isEmpty())
                ? "Transaction"
                : description;

        Transaction transaction = ModelMapper.transactionInputToTransaction(sender, recipient, senderWallet,
                recipientWallet, amount, descriptionToSave);

        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionOutput> filterTransactions(TransactionFilterOptions filterOptions) {
        Specification<Transaction> spec =
                TransactionSpecification.buildTransactionSpecification(filterOptions);

        Sort sort = convertToSort(filterOptions.getSortBy(), filterOptions.getSortOrder());
        Pageable pageable = PageRequest.of(filterOptions.getPage(), filterOptions.getSize(), sort);

        Page<Transaction> pageResult = transactionRepository.findAll(spec, pageable);

        return pageResult
                .stream()
                .map(ModelMapper::transactionToTransactionOutput)
                .toList();
    }

    @Override
    public Page<Transaction> filterTransactionsPage(TransactionFilterOptions filterOptions) {
        Specification<Transaction> spec =
                TransactionSpecification.buildTransactionSpecification(filterOptions);

        Sort sort = convertToSort(filterOptions.getSortBy(), filterOptions.getSortOrder());
        Pageable pageable = PageRequest.of(filterOptions.getPage(), filterOptions.getSize(), sort);

        return transactionRepository.findAll(spec, pageable);
    }
}