package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Currency;

public interface WalletServiceJPA {

    void update (Wallet wallet);

    Wallet getOrCreateWalletByUsernameAndCurrency(String userUsername, Currency currency);
}
