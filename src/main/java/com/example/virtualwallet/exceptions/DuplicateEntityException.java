package com.example.virtualwallet.exceptions;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String attribute) {
        super(String.format("%s already exists.", attribute));
    }

    public DuplicateEntityException(String attribute, String value) {
        super(String.format("%s with %s already exists.", attribute, value));
    }

    public DuplicateEntityException(String entity, String attribute, String value) {
        super(String.format("%s with %s: %s already exists.", entity, attribute, value));
    }
}
