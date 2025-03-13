package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.User;

import java.util.UUID;

public interface EmailConfirmationService {

    void createAndSendEmailConfirmationToUser(User user, boolean isRestRequest);

    void confirmEmailToken(UUID id);
}