package com.example.virtualwallet.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ADMIN,
    USER;

    @Override
    public String getAuthority() {
        return name();
    }

    @Override
    public String toString() {
        return this.name().substring(0, 1).toUpperCase()
                + this.name().substring(1).toLowerCase();
    }
}