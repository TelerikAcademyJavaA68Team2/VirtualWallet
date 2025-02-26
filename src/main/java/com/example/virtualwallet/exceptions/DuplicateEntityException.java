package com.example.virtualwallet.exceptions;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String type) {
        super(String.format("%s already exists.", type));
    }

    public DuplicateEntityException(String type, String attribute) {
        super(String.format("%s with %s already exists.", type, attribute));
    }
}
