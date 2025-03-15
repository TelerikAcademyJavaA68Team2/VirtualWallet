package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletsWithHistoryOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

import static com.example.virtualwallet.helpers.ValidationHelpers.requestIsWithInvalidPageOrSize;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mvc/profile/wallets")
public class WalletMvcController {

    private final WalletService walletService;
    private final UserService userService;
    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public String getWallets(@RequestParam(defaultValue = "BGN") String mainCurrency,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        if (requestIsWithInvalidPageOrSize(page, size)) {
            page = 0;
            size = 10;
        }
        WalletsWithHistoryOutput output = walletService.getActiveWalletsOfAuthenticatedUser(mainCurrency, page, size);
        List<String> walletCurrencies = output.getWallets().stream()
                .map(WalletBasicOutput::getCurrency)
                .toList();

        List<String> allCurrencies = Arrays.stream(Currency.values())
                .map(Enum::name)
                .filter(currency -> !walletCurrencies.contains(currency))
                .toList();
        model.addAttribute("wallets", output);
        model.addAttribute("mainCurrency", mainCurrency);
        model.addAttribute("allCurrencies",allCurrencies);
        model.addAttribute("currentUserUsername",userService.getAuthenticatedUser().getUsername());
        return "Wallets-View";
    }


}