package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface PasswordResetRepository extends JpaRepository<ResetPasswordToken, UUID> {

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM ResetPasswordToken e " +
            "WHERE e.user.email = :email AND e.expiresAt > :timeThreshold")
    boolean checkIfTokenWasAlreadySent(@Param("email") String email, @Param("timeThreshold") LocalDateTime timeThreshold);

}
