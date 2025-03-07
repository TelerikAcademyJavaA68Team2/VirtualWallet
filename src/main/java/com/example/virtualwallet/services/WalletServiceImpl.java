package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
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
    private final UserService userService;

    @Override
    public Wallet getWalletById(UUID id) {
        return walletRepository.findById(id);
    }


    @Override
    public Wallet getOrCreateWalletByUsernameAndCurrency(String userUsername, Currency currency) {
        Optional<Wallet> wallet = walletRepository.findByUsernameAndCurrency(userUsername, currency.toString());
        if (wallet.isPresent()) {
            if (wallet.get().isDeleted()) {
                wallet.get().restoreWallet();
            }
            return wallet.get();
        }
        Wallet newWallet = new Wallet();
        newWallet.setCurrency(currency);
        newWallet.setOwner(userService.loadUserByUsername(userUsername));
        walletRepository.create(newWallet);
        return newWallet;
    }

    @Override
    public List<WalletBasicOutput> getActiveWalletsOfAuthenticatedUser() {
        User user = userService.getAuthenticatedUser();
        List<Wallet> wallets = walletRepository.getActiveWalletsByUserId(user.getId());
        return wallets.stream().map(ModelMapper::mapWalletToBasicWalletOutput).toList();
    }

    @Override
    public WalletPageOutput getWalletPageById(UUID walletId, int page, int size) {
        WalletPageOutput pageOutput = walletRepository.getWalletHistory(walletId, page, size);
        Wallet wallet = walletRepository.findById(walletId);
        pageOutput.setBalance(wallet.getBalance());
        pageOutput.setCurrency(wallet.getCurrency());
        return pageOutput;
    }

    @Override
    public void softDeleteAuthenticatedUserWalletByCurrency(String currency) {
        User user = userService.getAuthenticatedUser();

        Optional<Wallet> wallet = walletRepository.findByUsernameAndCurrency(user.getUsername(), currency);
        if (wallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet", "Currency", currency);
        } else if (wallet.get().isDeleted()) {
            throw new InvalidUserInputException("Your wallet with currency:" + currency + " is already deleted!");
        }
        wallet.get().markAsDeleted();
    }

    @Override
    public void update(Wallet wallet) {
        walletRepository.update(wallet);
    }
}