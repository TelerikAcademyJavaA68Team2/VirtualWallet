package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.InsufficientFundsException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.transfer.TransactionInput;
import com.example.virtualwallet.models.dtos.transfer.TransactionOutput;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import com.example.virtualwallet.models.fillterOptions.TransactionSpecification;
import com.example.virtualwallet.repositories.TransactionRepository;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

        if(walletService.checkIfUserHasActiveWalletWithCurrency(sender.getId(), transactionCurrency)){
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

        walletService.update(senderWallet);
        walletService.update(recipientWallet);
        transactionRepository.save(transaction);
        Optional<Transaction> transactionToSave = transactionRepository.findById(transaction.getId());
        return ModelMapper.transactionToTransactionOutput(transactionToSave.get(), sender.getUsername(), recipientUsername );
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
    public List<TransactionOutput> findAllTransactionsByUserIdWithFilters(
            UUID userId, TransactionFilterOptions filterOptions) {

        // 1) Build the Specification
        Specification<Transaction> spec =
                TransactionSpecification.buildSpecification(userId, filterOptions);

        // 2) Convert sortBy, sortOrder to a Sort object
        Sort.Direction direction =
                filterOptions.getSortOrder().equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        //  decide which fields are valid. E.g. "date" or "amount"
        // to avoid letting the user supply something random that breaks your query.
        Sort sort = Sort.by(direction, filterOptions.getSortBy());

        // 3) Build the pageable
        Pageable pageable = PageRequest.of(
                filterOptions.getPage(),
                filterOptions.getSize(),
                sort
        );

        // 4) Fetch from repository
        Page<Transaction> pageResult = transactionRepository.findAll(spec, pageable);

        // 5) Map to whatever DTO/Output form you need
        //    Using .map(...) from Spring Data is easy, but you can also .stream()
        return pageResult
                .stream()
                .map(ModelMapper::transactionToTransactionOutput)
                .toList();
    }

    @Override
    public Set<Transaction> findAllTransactionsByWalletId(UUID walletId) {
        return null/* transactionRepository.findAllTransactionsByWalletId(walletId)*/;
    }
}