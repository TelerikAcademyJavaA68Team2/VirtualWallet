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

    @Query("""
            SELECT t FROM Transaction t
             WHERE t.recipientWallet.walletOwner.id = :userId
                 or t.recipientWallet.walletOwner.id = :userId
            """)
    Set<Transaction> findAllTransactionsByUserId(@Param("userId") UUID userId);

    @Query("""
                SELECT t FROM Transaction t
                WHERE t.senderWallet.id = :walletId
                   OR t.recipientWallet.id = :walletId
            """)
    Set<Transaction> findAllTransactionsByWalletId(@Param("walletId") UUID walletId);
}
