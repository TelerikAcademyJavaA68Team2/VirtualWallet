package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangePage;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import com.example.virtualwallet.services.contracts.ExchangeService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Controller
@RequestMapping("/mvc/profile/exchanges")
@RequiredArgsConstructor
public class ExchangeMvcController {

    private final ExchangeService exchangeService;
    private final ExchangeRateService exchangeRateService;
    private final WalletService walletService;
    private final UserService userService;


    @GetMapping("/{id}")
    public String getSingleExchangeView(@PathVariable UUID id, Model model) {
        FullExchangeInfoOutput exchange = exchangeService.getExchangeById(id);
        String recipientId = userService.findUserByUsernameOrEmailOrPhoneNumber(exchange.getRecipientUsername()).getId().toString();

        model.addAttribute("personalRequest", true);
        model.addAttribute("recipientId", recipientId);
        model.addAttribute("exchange", exchange);
        return "Exchange-View";
    }

    @GetMapping("/new")
    public String createExchange(
            @RequestParam(required = false) String fromCurrency,
            @RequestParam(required = false) String toCurrency,
            Model model) {
        String fromCurrencySanitized;
        String toCurrencySanitized;

        List<WalletBasicOutput> wallets = walletService.getActiveUserWalletsDto();

        if (wallets.isEmpty()){
            return "Exchange-Error-View";
        }

        List<Currency> currenciesList = Arrays.stream(Currency.values()).toList();
        Set<String> activeCurrencies = wallets.stream()
                .map(WalletBasicOutput::getCurrency)
                .collect(Collectors.toSet());

        if (fromCurrency != null && !fromCurrency.trim().isEmpty() && activeCurrencies.contains(fromCurrency.toUpperCase())) {
            fromCurrencySanitized = fromCurrency.toUpperCase();
        } else {
            fromCurrencySanitized = activeCurrencies.iterator().next();
        }


        List<String> otherActiveCurrencies = activeCurrencies.stream()
                .filter(e -> !e.equals(fromCurrencySanitized))
                .toList();


        List<String> otherInactiveCurrencies = currenciesList.stream()
                .map(Enum::toString)
                .filter(currency -> !activeCurrencies.contains(currency))
                .toList();

        if (toCurrency != null && !toCurrency.trim().isEmpty()) {
            if (otherActiveCurrencies.contains(toCurrency)) {
                toCurrencySanitized = toCurrency;
            } else if (otherInactiveCurrencies.contains(toCurrency)) {
                toCurrencySanitized = toCurrency;
            } else {
                if (wallets.size() >= 2) {
                    toCurrencySanitized = otherActiveCurrencies.iterator().next();
                } else {
                    toCurrencySanitized = otherInactiveCurrencies.iterator().next();
                }
            }
        } else {
            if (wallets.size() >= 2) {
                toCurrencySanitized = otherActiveCurrencies.iterator().next();
            } else {
                toCurrencySanitized = otherInactiveCurrencies.iterator().next();
            }
        }


        BigDecimal rate = exchangeRateService.getExchangeRate(fromCurrencySanitized, toCurrencySanitized).getRate().stripTrailingZeros();
        BigDecimal max = wallets.stream()
                .filter(e -> e.getCurrency().equals(fromCurrencySanitized))
                .findFirst()
                .map(WalletBasicOutput::getBalance)
                .orElse(BigDecimal.ZERO);


        ExchangeInput exchangeInput = new ExchangeInput();
        exchangeInput.setFromCurrency(fromCurrencySanitized);
        exchangeInput.setToCurrency(toCurrencySanitized);
        model.addAttribute("exchangeInput", exchangeInput);

        model.addAttribute("fromCurrency", fromCurrencySanitized);
        model.addAttribute("toCurrency", toCurrencySanitized);
        model.addAttribute("exchangeRate", rate);
        model.addAttribute("maxAmount", max);

        model.addAttribute("activeCurrencies", activeCurrencies);
        model.addAttribute("otherActiveCurrencies", otherActiveCurrencies);
        model.addAttribute("otherInactiveCurrencies", otherInactiveCurrencies);

        return "Create-Exchange-View";
    }

    @PostMapping("/new")
    public String executeExchange(@Valid ExchangeInput request) {

        exchangeService.createExchange(request);
        return "redirect:/mvc/profile/wallets";
    }


    @GetMapping
    public String getExchangesAndFilter(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String fromCurrency,
            @RequestParam(required = false) String toCurrency,
            @RequestParam(required = false) BigDecimal minStartAmount,
            @RequestParam(required = false) BigDecimal maxStartAmount,
            @RequestParam(required = false) BigDecimal minEndAmount,
            @RequestParam(required = false) BigDecimal maxEndAmount,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        if (validatePageOrSize(page, size)) {
            page = 0;
            size = 10;
        }

        fromDate = validateDateFromUrl(fromDate).orElse(null);
        toDate = validateDateFromUrl(toDate).orElse(null);

        String fromDateToDisplay = fromDate;
        String toDateToDisplay = toDate;
        if (fromDate != null && !fromDate.isEmpty() && !fromDate.endsWith("00")) {
            fromDate = fromDate.concat(" - 00:00");
            fromDate = convertToCustomFormat(fromDate);
        }
        if (toDate != null && !toDate.isEmpty() && !toDate.endsWith("00")) {
            toDate = toDate.concat(" - 00:00");
            toDate = convertToCustomFormat(toDate);
        }

        String recipientUsername = userService.getAuthenticatedUser().getUsername();
        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions(
                fromDate,
                toDate,
                fromCurrency,
                toCurrency,
                minStartAmount,
                maxStartAmount,
                minEndAmount,
                maxEndAmount,
                recipientUsername,
                sortBy,
                sortOrder,
                page,
                size);

        ExchangePage exchangePage = exchangeService.filterExchanges(filterOptions);
        List<String> currencies = Arrays.stream(Currency.values()).map((Enum::name)).toList();

        String sanitizedFromCurrency = sanitizeStringCurrency(fromCurrency).orElse(null);
        String sanitizedToCurrency = sanitizeStringCurrency(toCurrency).orElse(null);

        model.addAttribute("fromDate", fromDateToDisplay);
        model.addAttribute("toDate", toDateToDisplay);
        model.addAttribute("fromCurrency", sanitizedFromCurrency);
        model.addAttribute("toCurrency", sanitizedToCurrency);
        model.addAttribute("minStartAmount", minStartAmount);
        model.addAttribute("maxStartAmount", maxStartAmount);
        model.addAttribute("minEndAmount", minEndAmount);
        model.addAttribute("maxEndAmount", maxEndAmount);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        model.addAttribute("currencies", currencies);
        model.addAttribute("exchangePage", exchangePage);
        return "User-Exchanges-View";
    }

}