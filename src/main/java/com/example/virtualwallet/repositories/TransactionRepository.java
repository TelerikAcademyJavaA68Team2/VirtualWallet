package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT t FROM Transaction t WHERE t.id = :id AND (t.recipientUsername = :username or t.senderUsername = :username)")
    Optional<Transaction> findByIdAndUsername(@Param("id") UUID id, @Param("username") String username);

}