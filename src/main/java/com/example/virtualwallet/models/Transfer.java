package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.TransactionStatus;
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
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(generator = "UUID")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, precision = 38, scale = 2, name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "currency")
    private Currency currency;

    @Column(nullable = false, name = "date")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", referencedColumnName = "id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
    }
}
