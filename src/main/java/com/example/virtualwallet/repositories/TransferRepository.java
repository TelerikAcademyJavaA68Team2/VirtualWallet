package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID>, JpaSpecificationExecutor<Transfer> {

    @Query("SELECT t FROM Transfer t WHERE t.id = :id AND t.recipientUsername = :username")
    Optional<Transfer> findByIdAndUsername(@Param("id") UUID id, @Param("username") String username);
}
