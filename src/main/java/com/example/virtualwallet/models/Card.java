package com.example.virtualwallet.models;

import com.example.virtualwallet.helpers.YearMonthConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(generator = "UUID")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(unique = true, nullable = false, name = "card_number")
    private String cardNumber;

    @Column(nullable = false, name = "card_holder")
    private String cardHolder;

    @Convert(converter = YearMonthConverter.class)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)", name = "expiration_date")
    private YearMonth expirationDate;

    @Column(nullable = false, columnDefinition = "VARCHAR(5)", name = "cvv")
    private String cvv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    private Set<Transfer> transfers;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
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