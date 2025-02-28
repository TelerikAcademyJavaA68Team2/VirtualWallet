package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

   /* @Query("""
            SELECT DISTINCT t FROM Transaction t
            LEFT JOIN FETCH t.senderWallet sw
            LEFT JOIN FETCH sw.walletOwner swo
            LEFT JOIN FETCH t.recipientWallet rw
            LEFT JOIN FETCH rw.walletOwner rwo
            WHERE swo.id = :userId OR rwo.id = :userId
            """)
    Set<Transaction> findAllTransactionsByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT DISTINCT t FROM Transaction t
            LEFT JOIN FETCH t.senderWallet sw
            LEFT JOIN FETCH t.recipientWallet rw
            WHERE sw.id = :walletId OR rw.id = :walletId
            """)
    Set<Transaction> findAllTransactionsByWalletId(@Param("walletId") UUID walletId);*/
}