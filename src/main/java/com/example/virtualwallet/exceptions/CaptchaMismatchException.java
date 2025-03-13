package com.example.virtualwallet.exceptions;

public class CaptchaMismatchException extends RuntimeException {
    public CaptchaMismatchException(String message) {
        super(message);
    }
}
