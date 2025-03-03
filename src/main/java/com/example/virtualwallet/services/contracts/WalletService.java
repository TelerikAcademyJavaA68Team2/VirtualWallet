package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Currency;

import java.util.UUID;

public interface WalletService {

    Wallet getWalletById(UUID id);

    Wallet getOrCreateWalletByUsernameAndCurrency(String userUsername, Currency currency);

}