package com.example.virtualwallet.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false)
    private double balance;

    @Column(nullable = false)
    private String currency;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "wallet")
    private Set<Transaction> transactions;


}
