package com.example.virtualwallet.exceptions;

public class EmailConfirmationException extends RuntimeException {
    public EmailConfirmationException(String message) {
        super(message);
    }
}
