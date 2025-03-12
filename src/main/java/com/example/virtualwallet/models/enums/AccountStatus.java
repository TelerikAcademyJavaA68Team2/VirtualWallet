package com.example.virtualwallet.models.enums;

public enum AccountStatus {
    PENDING,
    ACTIVE,
    BLOCKED,
    BLOCKED_AND_DELETED,
    DELETED;

    @Override
    public String toString() {
        switch (this) {
            case BLOCKED_AND_DELETED:
                return "Blocked | Deleted";
            default:
                return this.name().substring(0, 1).toUpperCase()
                        + this.name().substring(1).toLowerCase();
        }
    }

}
