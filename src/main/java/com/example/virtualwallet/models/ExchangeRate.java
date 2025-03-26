package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "exchange_rate")
public class ExchangeRate {

    @Id
    @GeneratedValue(generator ="UUID" )
    private UUID id;

    @Column(nullable = false, name = "from_currency")
    @Enumerated(EnumType.STRING)
    private Currency fromCurrency;

    @Column(nullable = false, name = "to_currency")
    @Enumerated(EnumType.STRING)
    private Currency toCurrency;

    @Column(nullable = false, precision = 38, scale = 10, name = "rate")
    private BigDecimal rate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}