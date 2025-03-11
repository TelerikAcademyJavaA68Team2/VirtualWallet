package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, UUID>, JpaSpecificationExecutor<Exchange> {

    @Query("SELECT e FROM Exchange e WHERE e.recipientUsername = :username AND e.id = :id")
    Optional<Exchange> findByIdAndRecipientUsername(@Param("id") UUID id, @Param("username") String username);

}