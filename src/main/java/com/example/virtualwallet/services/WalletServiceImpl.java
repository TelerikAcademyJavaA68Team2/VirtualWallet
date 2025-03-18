package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import com.example.virtualwallet.models.dtos.wallet.ActivityOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletsWithHistoryOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.WalletRepository;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.validateAndConvertCurrency;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    public static final String EXCHANGE_REMAINING_CURRENCY = "Please exchange your remaining balance before deleting";

    private final WalletRepository walletRepository;
    private final ExchangeRateService exchangeRateService;
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
        return newWallet;
    }

    @Override
    public WalletsWithHistoryOutput getActiveWalletsOfAuthenticatedUser(String mainCurrency, int page, int size) {
        User user = userService.getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);

        List<Wallet> listWallets = walletRepository.findActiveWalletsByUserId(user.getId());
        Page<Object[]> queryResult = walletRepository.findUserWalletHistory(user.getUsername(), pageable);

        List<ActivityOutput> history = queryResult.stream().map(ModelMapper::mapObjectToActivity).toList();
        List<WalletBasicOutput> wallets = listWallets.stream().map(ModelMapper::mapWalletToBasicWalletOutput).toList();
        BigDecimal estimatedBalance;

        String estimatedCurrency = mainCurrency;
        if (wallets.isEmpty()) {
            estimatedBalance = BigDecimal.ZERO;
        } else if (wallets.stream().noneMatch(wallet -> wallet.getCurrency().equals(mainCurrency))) {
            estimatedCurrency = wallets.get(0).getCurrency();
            estimatedBalance = exchangeRateService.findCurrentBalanceByCurrency(mainCurrency, wallets);
        } else {
            estimatedBalance = exchangeRateService.findCurrentBalanceByCurrency(mainCurrency, wallets);
        }

        WalletsWithHistoryOutput output = new WalletsWithHistoryOutput();
        output.setEstimatedBalance(estimatedBalance);
        output.setEstimatedCurrency(estimatedCurrency);
        output.setWallets(wallets);
        output.setHistory(history);

        output.setHasNextPage(queryResult.hasNext());
        output.setHasPreviousPage(queryResult.hasPrevious());
        output.setPageSize(size);
        output.setTotalPages(queryResult.getTotalPages());
        output.setTotalElements(queryResult.getTotalElements());
        output.setCurrentPage(page);
        return output;
    }

    @Override
    public WalletPageOutput getWalletPageById(UUID walletId, int page, int size) {
        User user = userService.getAuthenticatedUser();

        if (!walletRepository.checkIfUserHasActiveWalletWithId(user.getId(), walletId)) {
            throw new EntityNotFoundException("Wallet", walletId);
        }
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new EntityNotFoundException("Wallet", walletId));

        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> queryResult = walletRepository.findWalletHistory(walletId, pageable);
        List<ActivityOutput> history = queryResult.stream().map(ModelMapper::mapObjectToActivity).toList();

        WalletPageOutput pageOutput = new WalletPageOutput();

        pageOutput.setHistory(history);
        pageOutput.setWalletId(walletId);
        pageOutput.setBalance(wallet.getBalance());
        pageOutput.setCurrency(wallet.getCurrency());
        pageOutput.setHistory(history);

        List<Wallet> listWallets = walletRepository.findActiveWalletsByUserId(user.getId());
        List<WalletBasicOutput> wallets = listWallets.stream().filter(e -> e.getId() != walletId).map(ModelMapper::mapWalletToBasicWalletOutput).toList();
        pageOutput.setWallets(wallets);

        pageOutput.setTotalElements(queryResult.getTotalElements());
        pageOutput.setCurrentPage(page);
        pageOutput.setTotalPages(queryResult.getTotalPages());
        pageOutput.setPageSize(size);
        pageOutput.setHasNextPage(queryResult.hasNext());
        pageOutput.setHasPreviousPage(queryResult.hasPrevious());
        return pageOutput;
    }

    @Override
    public void softDeleteAuthenticatedUserWalletById(UUID walletId) {
        User user = userService.getAuthenticatedUser();
        Optional<Wallet> wallet = walletRepository.findById(walletId);
        if (wallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet", walletId);
        } else if (!wallet.get().getOwner().equals(user)) {
            throw new UnauthorizedAccessException("You are not the owner of this wallet");
        } else if (wallet.get().isDeleted()) {
            throw new DuplicateEntityException("This wallet is already deleted!");
        } else if (wallet.get().getBalance().doubleValue() >= 0.01) {
            throw new InvalidUserInputException(EXCHANGE_REMAINING_CURRENCY);
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