package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmationToken, UUID> {

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM EmailConfirmationToken e " +
            "WHERE e.user.id = :userId AND e.createdAt > :timeThreshold")
    boolean checkIfTokenWasAlreadySent(@Param("userId") UUID userId, @Param("timeThreshold") LocalDateTime timeThreshold);

}