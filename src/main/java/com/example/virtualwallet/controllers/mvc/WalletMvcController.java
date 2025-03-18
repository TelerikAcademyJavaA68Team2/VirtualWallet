package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ValidationHelpers;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletsWithHistoryOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.requestIsWithInvalidPageOrSize;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mvc/profile/wallets")
public class WalletMvcController {

    private final WalletService walletService;
    private final UserService userService;

    @GetMapping
    public String getWallets(@RequestParam(defaultValue = "BGN") String mainCurrency,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        if (requestIsWithInvalidPageOrSize(page, size)) {
            page = 0;
            size = 10;
        }
        String sanitizedMainCurrency = ValidationHelpers.sanitizeStringCurrency(mainCurrency).orElse("BGN");
        WalletsWithHistoryOutput output = walletService.getActiveWalletsOfAuthenticatedUser(sanitizedMainCurrency, page, size);

        List<String> walletCurrencies = output.getWallets().stream()
                .map(WalletBasicOutput::getCurrency)
                .toList();
        List<String> otherCurrencies = Arrays.stream(Currency.values()).map(Enum::name).filter(e -> !walletCurrencies.contains(e)).toList();
        model.addAttribute("wallets", output);
        model.addAttribute("mainCurrency", output.getEstimatedCurrency());
        model.addAttribute("walletCurrencies", walletCurrencies);
        model.addAttribute("otherCurrencies", otherCurrencies);
        model.addAttribute("currentUserUsername", userService.getAuthenticatedUser().getUsername());
        return "Wallets-View";
    }

    @GetMapping("/{walletId}")
    public String getWalletPageById(@PathVariable UUID walletId,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        if (requestIsWithInvalidPageOrSize(page, size)) {
            page = 0;
            size = 10;
        }
        try {
            WalletPageOutput output = walletService.getWalletPageById(walletId, page, size);

            model.addAttribute("wallet", output);
            model.addAttribute("currentUserUsername", userService.getAuthenticatedUser().getUsername());
            return "Wallet-View";
        } catch (EntityNotFoundException e) {
            return "redirect:/mvc/profile/wallets";
        }

    }


    @GetMapping("/new")
    public String addNewWallet(@RequestParam String currency) {
        try {
            walletService.createAuthenticatedUserWalletWalletByCurrency(currency);
        } catch (Exception ignored) {
        }
        return "redirect:/mvc/profile/wallets";
    }

    @GetMapping("/{walletId}/delete")
    public String deleteWallet(@PathVariable UUID walletId) {
        try {
            walletService.softDeleteAuthenticatedUserWalletById(walletId);
            return "redirect:/mvc/profile/wallets";
        } catch (InvalidUserInputException e) {
            // wallet is with active balance >0 add error code
            return "redirect:/mvc/profile/wallets";
        } catch (EntityNotFoundException | UnauthorizedAccessException | DuplicateEntityException ignored) {
            return "redirect:/mvc/profile/wallets";
        }
    }
}