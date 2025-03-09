package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Exchange;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangeOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import com.example.virtualwallet.models.fillterOptions.TransactionSpecification;
import com.example.virtualwallet.repositories.ExchangeRepository;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import com.example.virtualwallet.services.contracts.ExchangeService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import com.example.virtualwallet.services.specifications.ExchangeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    public List<ExchangeOutput> filterExchanges(ExchangeFilterOptions filterOptions) {
        Specification<Exchange> spec =
                ExchangeSpecification.buildExchangeSpecification(filterOptions);

        Sort.Direction direction = filterOptions.getSortOrder().equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, filterOptions.getSortBy());

        Pageable pageable = PageRequest.of(
                filterOptions.getPage(),
                filterOptions.getSize(),
                sort
        );

        Page<Exchange> pageResult = exchangeRepository.findAll(spec, pageable);

        return pageResult
                .stream()
                .map(ModelMapper::exchangeToExchangeOutput)
                .toList();
    }

    @Override
    public void createExchange(ExchangeInput input) {
        validateExchangeInput(input);
        User user = userService.getAuthenticatedUser();

        Currency fromCurrency = Currency.valueOf(input.getFromCurrency().toUpperCase());
        Currency toCurrency = Currency.valueOf(input.getToCurrency().toUpperCase());

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
        exchange.setToAmount(amountToAdd);
        exchange.setFromCurrency(fromCurrency);
        exchange.setToCurrency(toCurrency);
        exchange.setRecipientUsername(user.getUsername());
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
