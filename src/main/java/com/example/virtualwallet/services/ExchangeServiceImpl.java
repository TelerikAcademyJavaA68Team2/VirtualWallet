package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientFundsException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Exchange;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangePage;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
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
import java.util.UUID;

import static com.example.virtualwallet.helpers.ModelMapper.convertToSort;
import static com.example.virtualwallet.helpers.ModelMapper.exchangeToFullExchangeOutput;
import static com.example.virtualwallet.helpers.ValidationHelpers.validateAndConvertCurrency;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    public static final String INVALID_CURRENCY = "Invalid currency provided";

    private final ExchangeRepository exchangeRepository;
    private final ExchangeRateService exchangeRateService;
    private final WalletService walletService;
    private final UserService userService;

    @Override
    public FullExchangeInfoOutput getExchangeById(UUID id) {
        User user = userService.getAuthenticatedUser();

        Exchange exchange;
        if (user.getRole().equals(Role.ADMIN)) {
            exchange = exchangeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Exchange", id));
        } else {
            exchange = exchangeRepository.findByIdAndRecipientUsername(id, user.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("Exchange", id));
        }

        return exchangeToFullExchangeOutput(exchange);
    }

    @Override
    public ExchangePage filterExchanges(ExchangeFilterOptions filterOptions) {
        Specification<Exchange> spec =
                ExchangeSpecification.buildExchangeSpecification(filterOptions);

        Sort sort = convertToSort(filterOptions.getSortBy(), filterOptions.getSortOrder());

        Pageable pageable = PageRequest.of(
                filterOptions.getPage(),
                filterOptions.getSize(),
                sort
        );

        Page<Exchange> pageResult = exchangeRepository.findAll(spec, pageable);

        ExchangePage pageOutput = new ExchangePage();
        pageOutput.setExchanges(pageResult
                .stream()
                .map(ModelMapper::exchangeToExchangeOutput)
                .toList());

        pageOutput.setTotalElements(pageResult.getTotalElements());
        pageOutput.setCurrentPage(filterOptions.getPage());
        pageOutput.setTotalPages(pageResult.getTotalPages());
        pageOutput.setPageSize(filterOptions.getSize());
        pageOutput.setHasNextPage(pageResult.hasNext());
        pageOutput.setHasPreviousPage(pageResult.hasPrevious());
        return pageOutput;
    }

    @Override
    public void createExchange(ExchangeInput input) {
        validateCurrencies(input);
        Currency fromCurrency = validateAndConvertCurrency(input.getFromCurrency());
        Currency toCurrency = validateAndConvertCurrency(input.getToCurrency());

        User user = userService.getAuthenticatedUser();
        if (!walletService.checkIfUserHasActiveWalletWithCurrency(user.getId(), fromCurrency)) {
            throw new EntityNotFoundException("Wallet", "currency", fromCurrency.name());
        }

        Wallet fromWallet = walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(), fromCurrency);
        Wallet toWallet = walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(), toCurrency);
        BigDecimal exchangeRate = exchangeRateService.getRate(fromCurrency, toCurrency);

        BigDecimal amountToRemove = input.getAmount();
        BigDecimal amountToAdd = amountToRemove.multiply(exchangeRate);

        if (fromWallet.getBalance().compareTo(amountToRemove) < 0) {
            throw new InsufficientFundsException("Insufficient balance in " + fromCurrency + " wallet");
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

    private void validateCurrencies(ExchangeInput input) {
        if (input.getFromCurrency().equalsIgnoreCase(input.getToCurrency())) {
            throw new InvalidUserInputException(INVALID_CURRENCY);
        }
    }
}
