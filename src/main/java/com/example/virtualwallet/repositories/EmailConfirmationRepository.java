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
            "WHERE e.user.email = :email AND e.createdAt > :timeThreshold")
    boolean checkIfTokenWasAlreadySent(@Param("email") String email, @Param("timeThreshold") LocalDateTime timeThreshold);

}