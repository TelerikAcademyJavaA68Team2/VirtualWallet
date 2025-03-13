package com.example.virtualwallet.services.contracts;

public interface EmailService {

    void sendVerificationEmail(String firstName, String toEmail, String tokenId,boolean IsRestRequest);
}
