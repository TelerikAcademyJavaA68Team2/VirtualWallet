package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferInputMVC;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mvc/profile/wallets")
public class TransferMvcController {

    private final UserService userService;
    private final WalletService walletService;
    private final CardService cardService;
    private final TransferService transferService;

    @GetMapping("/fund-wallet")
    public String showFundWalletForm(Model model) {

        User user = userService.getAuthenticatedUser();


        List<Card> cards = cardService.getAllCardsByUser(user.getId());
        List<Wallet> wallets = walletService.getActiveWalletsOfUser(user.getId());


        TransferInputMVC transferInput = new TransferInputMVC();


        model.addAttribute("currencies", Currency.values());
        model.addAttribute("transferInput", transferInput);
        model.addAttribute("cards", cards);
        model.addAttribute("wallets", wallets);

        return "Fund-Wallet-View";
    }


    @PostMapping("/fund-wallet")
    public String fundWallet(@Valid TransferInputMVC transferInput,
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

        // On success, redirect or display success message
    }
}

