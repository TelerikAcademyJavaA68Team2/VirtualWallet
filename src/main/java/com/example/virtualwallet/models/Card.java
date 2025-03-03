package com.example.virtualwallet.models;

import com.example.virtualwallet.helpers.YearMonthConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("is_deleted = false")
public class Card {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String cardHolder;

    @Convert(converter = YearMonthConverter.class)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private YearMonth expirationDate;

    @Column(nullable = false, columnDefinition = "VARCHAR(5)")
    private String cvv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    private Set<Transfer> transfers;

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
    public void markAsRestored() {
        this.deletedAt = null;
        this.isDeleted = false;
    }
}