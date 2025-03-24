package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletsWithHistoryOutput;
import com.example.virtualwallet.models.enums.Currency;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    Wallet getWalletById(UUID id);

    Wallet getOrCreateWalletByUsernameAndCurrency(String userUsername, Currency currency);

    void update(Wallet wallet);

    WalletsWithHistoryOutput getActiveWalletsOfAuthenticatedUser(String mainCurrency, int page, int size);

    List<Wallet> getActiveWalletsOfUser(UUID user);

    List<WalletBasicOutput> getActiveUserWalletsDto();

    WalletPageOutput getWalletPageById(UUID walletId, int page, int size);

    void softDeleteAuthenticatedUserWalletById(UUID walletId);

    boolean checkIfUserHasActiveWalletWithCurrency(UUID userId, Currency currency);

    void createAuthenticatedUserWalletWalletByCurrency(String currency);
}