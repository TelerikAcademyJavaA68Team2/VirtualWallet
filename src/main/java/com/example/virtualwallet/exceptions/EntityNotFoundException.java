package com.example.virtualwallet.exceptions;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String type) {
        super(String.format("%s not found.", type));
    }

    public EntityNotFoundException(String type, UUID id) {
        super(String.format("%s with id %s not found.", type, id));
    }
}