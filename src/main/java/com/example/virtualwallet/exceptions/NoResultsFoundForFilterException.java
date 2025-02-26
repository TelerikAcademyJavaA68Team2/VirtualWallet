package com.example.virtualwallet.exceptions;

public class NoResultsFoundForFilterException extends RuntimeException {
    public NoResultsFoundForFilterException(String message) {
        super(message);
    }
}
