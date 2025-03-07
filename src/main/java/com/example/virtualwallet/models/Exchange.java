package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "exchange")
public class Exchange {

    @Id
    @GeneratedValue(generator = "UUID")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, name= "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(nullable = false, precision = 15, scale = 2, name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "from_currency")
    private Currency fromCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "to_currency")
    private Currency toCurrency;

    @Column(nullable = false, name = "date")
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