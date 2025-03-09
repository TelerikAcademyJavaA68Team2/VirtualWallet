package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.Exchange;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangeOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.repositories.ExchangeRepository;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import com.example.virtualwallet.services.contracts.ExchangeService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final Set<String> validExchanges = Set.of("EUR", "USD", "BGN");

    private final ExchangeRepository exchangeRepository;
    private final ExchangeRateService exchangeRateService;
    private final WalletService walletService;
    private final UserService userService;


    @Override
    public List<ExchangeOutput> filterExchanges() {
        return List.of();
    }

    @Override
    @Transactional
    public void createExchange(ExchangeInput input) {
        validateExchangeInput(input);
        User user = userService.getAuthenticatedUser();

        Currency fromCurrency = Currency.valueOf(input.getFromCurrency());
        Currency toCurrency = Currency.valueOf(input.getToCurrency());

        if (!walletService.checkIfUserHasActiveWalletWithCurrency(user.getId(), fromCurrency)) {
            throw new EntityNotFoundException("Wallet", "currency", fromCurrency.name());
        }

        Wallet fromWallet = walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(), fromCurrency);
        Wallet toWallet = walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(), toCurrency);
        BigDecimal exchangeRate = exchangeRateService.getRate(fromCurrency, toCurrency);

        BigDecimal amountToRemove = input.getAmount();
        BigDecimal amountToAdd = amountToRemove.multiply(exchangeRate);

        if (fromWallet.getBalance().compareTo(amountToRemove) < 0) {
            throw new InvalidUserInputException("Insufficient balance in " + fromCurrency + " wallet");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(amountToRemove));
        toWallet.setBalance(toWallet.getBalance().add(amountToAdd));

        Exchange exchange = new Exchange();
        exchange.setExchangeRate(exchangeRate);
        exchange.setAmount(input.getAmount());
        exchange.setFromCurrency(fromCurrency);
        exchange.setToCurrency(toCurrency);
        exchange.setFromWallet(fromWallet);
        exchange.setToWallet(toWallet);

        walletService.update(fromWallet);
        walletService.update(toWallet);
        exchangeRepository.save(exchange);
    }

    private void validateExchangeInput(ExchangeInput input) {
        if (!validExchanges.contains(input.getFromCurrency().toUpperCase()) ||
                !validExchanges.contains(input.getToCurrency().toUpperCase())) {
            throw new InvalidUserInputException("Invalid currency provided");
        }
    }
}
