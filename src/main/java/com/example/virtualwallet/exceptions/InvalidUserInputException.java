package com.example.virtualwallet.exceptions;

import java.time.format.DateTimeParseException;

public class InvalidUserInputException extends RuntimeException {
    public InvalidUserInputException(String message) {
        super(message);
    }

    public InvalidUserInputException(String message, DateTimeParseException ex) {
        super(message);
    }
}
