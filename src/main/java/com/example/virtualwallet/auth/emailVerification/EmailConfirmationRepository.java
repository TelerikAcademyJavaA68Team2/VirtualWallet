package com.example.virtualwallet.auth.emailVerification;

import com.example.virtualwallet.models.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmationToken, UUID> {

}