package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.WalletRepository;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    public static final String NOT_WALLET_OWNER = "You are not the wallet's owner!";
    private final WalletRepository walletRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    public Wallet getWalletById(UUID id) {
        return walletRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Wallet", id));
    }
/*
    @Override
*/
    public Wallet getWalletActivityById(UUID walletId) {
        return walletRepository.findById(walletId).orElseThrow(() -> new EntityNotFoundException("Wallet", walletId));
    }

    @Override
    public Wallet getOrCreateWalletByUsernameAndCurrency(String userUsername, Currency currency) {
        Optional<Wallet> wallet = walletRepository.findByUsernameAndCurrency(userUsername, currency);
        if (wallet.isPresent()) {
            return wallet.get();
        }
        Wallet newWallet = new Wallet();
        newWallet.setCurrency(currency);
        newWallet.setOwner(userService.loadUserByUsername(userUsername));
        walletRepository.save(newWallet);
        return newWallet;
    }

    @Override
    public List<WalletBasicOutput> getActiveWalletsByUserId(UUID user_id) {
        List<Wallet> wallets = walletRepository.findActiveWalletsByUserId(user_id);
        return wallets.stream().map(modelMapper::mapWalletToBasicWalletOutput).toList();
    }

    @Override
    public void softDeleteAuthenticatedUserWalletByCurrency(Currency currency) {
        User user = userService.getAuthenticatedUser();

        Optional<Wallet> wallet = walletRepository.findByUsernameAndCurrency(user.getUsername(), currency);
        if (wallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet", "Currency", currency.name());
        } else if (wallet.get().isDeleted()) {
            throw new InvalidUserInputException("Your wallet with currency:" + currency.name() + " is already deleted!");
        }
        wallet.get().markAsDeleted();
    }

    @Override
    public void update(Wallet wallet, User user) {
        if(wallet.getOwner().equals(user)){
            walletRepository.save(wallet);
        } else throw new UnauthorizedAccessException(NOT_WALLET_OWNER);
    }

}
