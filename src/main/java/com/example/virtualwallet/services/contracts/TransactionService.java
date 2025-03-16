package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.dtos.transactions.FullTransactionInfoOutput;
import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionOutput createTransaction(TransactionInput transaction);

    FullTransactionInfoOutput getTransactionById(UUID id);

    List<TransactionOutput> filterTransactions(TransactionFilterOptions filterOptions);

    Page<Transaction> filterTransactionsPage(TransactionFilterOptions filterOptions);
}