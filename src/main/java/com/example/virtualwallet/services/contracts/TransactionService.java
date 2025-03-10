package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;

import java.util.List;

public interface TransactionService {

    TransactionOutput createTransaction(TransactionInput transaction);

    List<TransactionOutput> filterTransactions(TransactionFilterOptions filterOptions);
}