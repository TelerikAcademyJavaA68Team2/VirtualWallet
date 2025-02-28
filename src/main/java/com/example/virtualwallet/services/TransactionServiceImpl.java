package com.example.virtualwallet.services;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.repositories.TransactionRepository;
import com.example.virtualwallet.services.contracts.TransactionService;

import java.util.Set;
import java.util.UUID;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    @Override
    public void createTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public Set<Transaction> findAllTransactionsByUserId(UUID userId) {
        return null/* transactionRepository.findAllTransactionsByUserId(userId)*/;
    }

    @Override
    public Set<Transaction> findAllTransactionsByWalletId(UUID walletId) {
        return null/* transactionRepository.findAllTransactionsByWalletId(walletId)*/;
    }
}