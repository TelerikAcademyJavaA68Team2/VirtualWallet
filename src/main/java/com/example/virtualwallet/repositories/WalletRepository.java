package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import com.example.virtualwallet.models.enums.Currency;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {

    Wallet findById(UUID id);

    void create(Wallet wallet);

    void update(Wallet wallet);

    Optional<Wallet> findByUsernameAndCurrency(String userUsername, String currency);

    WalletPageOutput getWalletHistory(UUID walletId, int page, int size);

    List<Wallet> getActiveWalletsByUserId(UUID userId);

    boolean checkIfUserHasWalletWithCurrency(UUID userId, Currency currency);
}