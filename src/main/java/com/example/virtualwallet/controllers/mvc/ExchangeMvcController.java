package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.models.dtos.exchange.ExchangePage;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import com.example.virtualwallet.services.contracts.ExchangeService;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Controller
@RequestMapping("/mvc/profile/exchanges")
@RequiredArgsConstructor
public class ExchangeMvcController {

    private final ExchangeService exchangeService;
    private final UserService userService;

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
        if (requestIsWithInvalidPageOrSize(page, size)) {
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

        model.addAttribute("fromDate", fromDateToDisplay);
        model.addAttribute("toDate", toDateToDisplay);
        model.addAttribute("fromCurrency", fromCurrency);
        model.addAttribute("toCurrency", toCurrency);
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