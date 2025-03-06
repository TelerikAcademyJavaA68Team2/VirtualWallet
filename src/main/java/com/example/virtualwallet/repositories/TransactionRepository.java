package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
            SELECT DISTINCT t FROM Transaction t
            LEFT JOIN FETCH t.senderWallet sw
            LEFT JOIN FETCH t.recipientWallet rw
            WHERE sw.id = :walletId OR rw.id = :walletId
            """)
    Set<Transaction> findAllTransactionsByWalletId(@Param("walletId") UUID walletId);

    List<Transaction> findAllTransactionsBySenderWallet_Owner_IdOrRecipientWallet_Owner_Id(UUID senderWalletOwnerId,
                                                                                           UUID recipientWalletOwnerId,
                                                                                           Sort sort);
}