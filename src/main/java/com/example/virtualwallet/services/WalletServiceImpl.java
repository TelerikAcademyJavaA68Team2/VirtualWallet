package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import com.example.virtualwallet.models.dtos.wallet.ActivityOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.JPAWalletRepository;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.validateAndConvertCurrency;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final JPAWalletRepository walletRepository;
    private final UserService userService;

    @Override
    public Wallet getWalletById(UUID id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", id));
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
        return newWallet; // todo check if this will return the id of the new wallet
    }

    @Override
    public List<WalletBasicOutput> getActiveWalletsOfAuthenticatedUser() {
        User user = userService.getAuthenticatedUser();
        List<Wallet> wallets = walletRepository.findActiveWalletsByUserId(user.getId());
        return wallets.stream().map(ModelMapper::mapWalletToBasicWalletOutput).toList();
    }

    @Override
    public WalletPageOutput getWalletPageById(String currency, int page, int size) {
        User user = userService.getAuthenticatedUser();
        Currency enumCurrency = validateAndConvertCurrency(currency);
        if (!checkIfUserHasActiveWalletWithCurrency(user.getId(), enumCurrency)) {
            throw new EntityNotFoundException("Wallet", "currency", currency);
        }
        Wallet wallet = getOrCreateWalletByUsernameAndCurrency(user.getUsername(), enumCurrency);
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> queryResult = walletRepository.findWalletHistory(wallet.getId(), pageable);

        List<ActivityOutput> history = queryResult.stream().map(ModelMapper::mapObjectToActivity).toList();

        WalletPageOutput pageOutput = new WalletPageOutput();
        pageOutput.setHistoryPages(queryResult.getTotalPages());
        pageOutput.setHistorySize(queryResult.getTotalElements());
        pageOutput.setActivities(history);
        pageOutput.setBalance(wallet.getBalance());
        pageOutput.setCurrency(wallet.getCurrency());
        return pageOutput;
    }

    @Override
    public void softDeleteAuthenticatedUserWalletByCurrency(String currency) {
        Currency enumCurrency = validateAndConvertCurrency(currency);
        User user = userService.getAuthenticatedUser();
        Optional<Wallet> wallet = walletRepository.findByUsernameAndCurrency(user.getUsername(), enumCurrency);
        if (wallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet", "Currency", currency);
        } else if (wallet.get().isDeleted()) {
            throw new InvalidUserInputException("Your wallet with currency: " + currency + " is already deleted!");
        } else if (wallet.get().getBalance().doubleValue() >= 0.01) {
            throw new InvalidUserInputException("Please exchange your remaining balance before deleting");
        }
        wallet.get().markAsDeleted();
        walletRepository.save(wallet.get());
    }

    @Override
    public boolean checkIfUserHasActiveWalletWithCurrency(UUID userId, Currency currency) {
        return walletRepository.checkIfUserHasActiveWalletWithCurrency(userId, currency);
    }

    @Override
    public void createAuthenticatedUserWalletWalletByCurrency(String currency) {
        Currency enumCurrency = validateAndConvertCurrency(currency);
        User user = userService.getAuthenticatedUser();
        if (walletRepository.checkIfUserHasActiveWalletWithCurrency(user.getId(), enumCurrency)) {
            throw new DuplicateEntityException("Wallet", "Currency", currency);
        }
        getOrCreateWalletByUsernameAndCurrency(user.getUsername(), enumCurrency);
    }

    @Override
    public void update(Wallet wallet) {
        walletRepository.save(wallet);
    }
}