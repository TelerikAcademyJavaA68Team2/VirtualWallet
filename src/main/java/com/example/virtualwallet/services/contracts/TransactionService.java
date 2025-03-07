package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.dtos.transfer.TransactionInput;
import com.example.virtualwallet.models.dtos.transfer.TransactionOutput;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TransactionService {

    TransactionOutput createTransaction(TransactionInput transaction);

    List<TransactionOutput> findAllTransactionsByUserId(UUID userId);

    List<TransactionOutput> findAllTransactionsByUserIdWithFilters(UUID userId, TransactionFilterOptions filterOptions);

    Set<Transaction> findAllTransactionsByWalletId(UUID walletId);
}