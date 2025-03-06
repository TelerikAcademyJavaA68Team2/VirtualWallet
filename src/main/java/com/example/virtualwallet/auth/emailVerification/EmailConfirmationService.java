package com.example.virtualwallet.auth.emailVerification;

import com.example.virtualwallet.models.EmailConfirmationToken;

import java.util.UUID;

public interface EmailConfirmationService {

    void save(EmailConfirmationToken token);

    void confirmEmailToken(UUID id);
}
