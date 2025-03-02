package com.example.virtualwallet.auth.emailVerification;

public interface EmailService {

    void sendVerificationEmail(String firstName, String toEmail, String tokenId);
}
