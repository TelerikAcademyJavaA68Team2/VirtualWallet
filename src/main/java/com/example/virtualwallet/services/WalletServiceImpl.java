package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.UserRepository;
import com.example.virtualwallet.repositories.WalletRepository;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Override
    public Wallet getWalletById(UUID id) {
        return walletRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Wallet", id));
    }

    @Override
    public Wallet getOrCreateWalletByUsernameAndCurrency(String userUsername, Currency currency) {
        Optional<Wallet> wallet = walletRepository.findByUsernameAndCurrency(userUsername, currency);
        if (wallet.isPresent()) {
            return wallet.get();
        }
        Wallet newWallet = new Wallet();
        newWallet.setCurrency(currency);
        newWallet.setOwner(userRepository.findByUsername(userUsername).orElseThrow(
                () -> new EntityNotFoundException("User", "Username", userUsername)));
        walletRepository.save(newWallet);
        return newWallet;
    }


}
