package com.example.virtualwallet.services;

import com.example.virtualwallet.models.Transaction;

import java.util.Set;
import java.util.UUID;

public interface TransactionService {

    void createTransaction(Transaction transaction);

    Set<Transaction> findAllTransactionsByUserId(UUID userId);

    Set<Transaction> findAllTransactionsByWalletId(UUID walletId);
}