package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TransactionService {

    TransactionOutput createTransaction(TransactionInput transaction);

    List<TransactionOutput> findAllTransactionsByUserId(UUID userId);

    Set<Transaction> findAllTransactionsByWalletId(UUID walletId);

    List<TransactionOutput> findAllTransactionsWithFilters(TransactionFilterOptions filterOptions);
}