package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ValidationHelpers;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferInputMVC;
import com.example.virtualwallet.models.dtos.wallet.WalletBasicOutput;
import com.example.virtualwallet.models.dtos.wallet.WalletsWithHistoryOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private final CardService cardService;
    private final TransferService transferService;

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
            UUID walletId = walletService.createAuthenticatedUserWalletWalletByCurrency(currency).getId();
            return "redirect:/mvc/profile/wallets/" + walletId;
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

    @GetMapping("/fund-wallet")
    public String showFundWalletForm(Model model) {

        User user = userService.getAuthenticatedUser();


        List<Card> cards = cardService.getAllCardsByUser(user.getId());
        List<Wallet> wallets = walletService.getActiveWalletsOfUser(user.getId());


        TransferInputMVC transferInput = new TransferInputMVC();

        model.addAttribute("transferInput", transferInput);
        model.addAttribute("cards", cards);
        model.addAttribute("wallets", wallets);

        return "Fund-Wallet-View";
    }


    @PostMapping("/fund-wallet")
    public String fundWallet(@Valid @ModelAttribute("transferInput") TransferInputMVC transferInput,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {

            User user = userService.getAuthenticatedUser();
            model.addAttribute("cards", cardService.getAllCardsByUser(user.getId()));
            model.addAttribute("wallets", walletService.getActiveWalletsOfUser(user.getId()));
            return "Fund-Wallet-View";
        }

        try {

            FullTransferInfoOutput transferInfoOutput = transferService.processTransfer(transferInput);

            if (transferInfoOutput.getStatus().equals(TransactionStatus.APPROVED.toString())) {
                model.addAttribute("amount", transferInfoOutput.getAmount());
                model.addAttribute("currency", transferInfoOutput.getCurrency());
                return "Funding-Successful-View";
            } else {
                return "Funding-Declined-View";
            }
        } catch (Exception ex) {

            model.addAttribute("error", ex.getMessage());


            User user = userService.getAuthenticatedUser();
            model.addAttribute("cards", cardService.getAllCardsByUser(user.getId()));
            model.addAttribute("wallets", walletService.getActiveWalletsOfUser(user.getId()));
            return "Fund-Wallet-View";
        }
    }
}