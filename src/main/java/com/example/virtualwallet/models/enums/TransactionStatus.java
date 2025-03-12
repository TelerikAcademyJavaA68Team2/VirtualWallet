package com.example.virtualwallet.models.enums;

public enum TransactionStatus {

    APPROVED,
    DECLINED;

    @Override
    public String toString() {
        return this.name().substring(0, 1).toUpperCase()
                + this.name().substring(1).toLowerCase();
    }
}