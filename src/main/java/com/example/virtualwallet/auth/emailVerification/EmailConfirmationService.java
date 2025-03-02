package com.example.virtualwallet.auth.emailVerification;

import java.util.UUID;

public interface EmailConfirmationService {

    void save(EmailConfirmationToken token);

    void confirmEmailToken(UUID id);
}
