package com.example.virtualwallet.services;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.JPAWalletRepository;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletServiceJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WalletServiceJPAImpl implements WalletServiceJPA {

    private final JPAWalletRepository walletRepository;
    private final UserService userService;

    @Override
    public void update(Wallet wallet) {
        walletRepository.save(wallet);
    }

    @Override
    public Wallet getOrCreateWalletByUsernameAndCurrency(String userUsername, Currency currency) {
        Optional<Wallet> wallet = walletRepository.findByUsernameAndCurrency(userUsername, currency);
        if (wallet.isPresent()) {
            if (wallet.get().isDeleted()) {
                wallet.get().restoreWallet();
                walletRepository.save(wallet.get());
            }
            return wallet.get();
        }
        Wallet newWallet = new Wallet();
        newWallet.setCurrency(currency);
        newWallet.setOwner(userService.loadUserByUsername(userUsername));
        walletRepository.save(newWallet);
        return newWallet;
    }
}
