package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Currency;
import jakarta.persistence.*;
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
public class Wallet {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "senderWallet", fetch = FetchType.LAZY)
    private Set<Transaction> sentTransactions = new HashSet<>();

    @OneToMany(mappedBy = "recipientWallet", fetch = FetchType.LAZY)
    private Set<Transaction> receivedTransactions = new HashSet<>();

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    private Set<Transfer> transfers = new HashSet<>();

    @OneToMany(mappedBy = "fromWallet", fetch = FetchType.LAZY)
    private Set<Exchange> exchangesFromWallet = new HashSet<>();

    @OneToMany(mappedBy = "toWallet", fetch = FetchType.LAZY)
    private Set<Exchange> exchangesToWallet = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }
}