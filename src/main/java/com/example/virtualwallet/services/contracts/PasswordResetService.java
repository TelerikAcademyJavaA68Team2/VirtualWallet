package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.dtos.auth.NewPasswordResetInput;
import com.example.virtualwallet.models.dtos.auth.PasswordResetInput;

import java.util.UUID;

public interface PasswordResetService {

    void sendResetPasswordEmail(PasswordResetInput input, boolean isRestRequest);

    void processResetPasswordInput(NewPasswordResetInput input, UUID tokenId);

    boolean checkIfTokenExists(UUID tokenId);
}