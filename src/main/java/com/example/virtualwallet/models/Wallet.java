package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Currency;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Wallet {

    @Id
    @GeneratedValue(generator = "UUID")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "senderWallet", fetch = FetchType.LAZY)
    private Set<Transaction> sentTransactions;

    @OneToMany(mappedBy = "recipientWallet", fetch = FetchType.LAZY)
    private Set<Transaction> receivedTransactions;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    private Set<Transfer> transfers;

    @OneToMany(mappedBy = "fromWallet", fetch = FetchType.LAZY)
    private Set<Exchange> exchangesFromWallet;

    @OneToMany(mappedBy = "toWallet", fetch = FetchType.LAZY)
    private Set<Exchange> exchangesToWallet;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        this.balance = BigDecimal.ZERO;
        this.sentTransactions = new HashSet<>();
        this.receivedTransactions = new HashSet<>();
        this.transfers = new HashSet<>();
        this.exchangesFromWallet = new HashSet<>();
        this.exchangesToWallet = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void restoreWallet() {
        this.deletedAt = null;
        this.isDeleted = false;
    }
}