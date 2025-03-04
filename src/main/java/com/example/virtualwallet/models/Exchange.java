package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exchange {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false)
    private BigDecimal exchangeRate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency fromCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency toCurrency;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_wallet_id", nullable = false)
    private Wallet fromWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_wallet_id", nullable = false)
    private Wallet toWallet;

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
    }
}