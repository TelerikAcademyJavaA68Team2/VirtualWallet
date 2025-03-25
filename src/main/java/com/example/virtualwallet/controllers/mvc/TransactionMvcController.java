package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientFundsException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.transactions.FullTransactionInfoOutput;
import com.example.virtualwallet.models.dtos.transactions.TransactionInputMVC;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;
import static com.example.virtualwallet.helpers.ValidationHelpers.validateDateFromUrl;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mvc/profile/transactions")
public class TransactionMvcController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final WalletService walletService;

    @GetMapping
    public String getUserTransactions(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String description,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        if (requestIsWithInvalidPageOrSize(page, size)) {
            page = 0;
            size = 10;
        }

        fromDate = validateDateFromUrl(fromDate).orElse(null);
        toDate = validateDateFromUrl(toDate).orElse(null);

        User user = userService.getAuthenticatedUser();
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
        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                user.getUsername(), fromDate, toDate, minAmount, maxAmount, currency,
                sender, recipient, direction, description, sortBy, sortOrder, page, size
        );

        List<String> currencies = Arrays.stream(Currency.values()).map((Enum::name)).toList();
        Page<Transaction> transactionsPage = transactionService.filterTransactionsPage(filterOptions);

        String sanitizedCurrency = sanitizeStringCurrency(currency).orElse(null);

        int startIndex = page * size;
        model.addAttribute("currencies", currencies);
        model.addAttribute("transactions", transactionsPage);
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("fromDate", fromDateToDisplay);
        model.addAttribute("toDate", toDateToDisplay);
        model.addAttribute("minAmount", minAmount);
        model.addAttribute("maxAmount", maxAmount);
        model.addAttribute("currency", sanitizedCurrency);
        model.addAttribute("sender", sender);
        model.addAttribute("recipient", recipient);
        model.addAttribute("direction", direction);
        model.addAttribute("description", description);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "User-Transactions-View";
    }

    @GetMapping("/{id}")
    public String getSingleTransactionView(Model model, @PathVariable UUID id) {
        FullTransactionInfoOutput transactionInfoOutput = transactionService.getTransactionById(id);
        String senderId = userService.findUserByUsernameOrEmailOrPhoneNumber(transactionInfoOutput.getSenderUsername()).getId().toString();
        String recipientId = userService.findUserByUsernameOrEmailOrPhoneNumber(transactionInfoOutput.getRecipientUsername()).getId().toString();

        model.addAttribute("senderId", senderId);
        model.addAttribute("recipientId", recipientId);
        model.addAttribute("transaction", transactionInfoOutput);
        return "Transaction-View";
    }

    @GetMapping("/new")
    public String showTransactionForm(Model model) {
        User sender = userService.getAuthenticatedUser();


        model.addAttribute("user", sender);
        model.addAttribute("transactionInput", new TransactionInputMVC());

        return "Transaction-Create-View";
    }

    @GetMapping("/send")
    public String findRecipient(@RequestParam String user,
                                Model model) {

        User sender = userService.getAuthenticatedUser();
        model.addAttribute("user", sender);

        try {
            User recipient = userService.findUserByUsernameOrEmailOrPhoneNumber(user);
            if (sender.getId().equals(recipient.getId())) {
                model.addAttribute("error", "You cannot send money to yourself.");
                return "Transaction-Create-View";
            }

            if (sender.getStatus().equals(AccountStatus.BLOCKED) ||
                    sender.getStatus().equals(AccountStatus.BLOCKED_AND_DELETED)) {
                model.addAttribute("error", "You are blocked and cannot make transactions. " +
                        "Please, contact our support at virtual.wallet.a68@gmail.com for more information.");
                return "Transaction-Create-View";
            }

            if (recipient.getStatus().equals(AccountStatus.BLOCKED) ||
                    recipient.getStatus().equals(AccountStatus.BLOCKED_AND_DELETED)) {
                model.addAttribute("error", "This user is blocked and cannot receive money.");
                return "Transaction-Create-View";
            }

            TransactionInputMVC transactionInput = new TransactionInputMVC();
            transactionInput.setRecipientId(recipient.getId());

            List<Wallet> activeWallets = walletService.getActiveWalletsOfUser(sender.getId());

            model.addAttribute("recipient", recipient);
            model.addAttribute("activeWallets", activeWallets);
            model.addAttribute("transactionInput", transactionInput);

            return "Transaction-Create-View"; // Refresh the view with recipient details
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "Transaction-Create-View";

        }
    }

    @PostMapping("/new")
    public String createTransaction(@Valid @ModelAttribute("transactionInput") TransactionInputMVC transactionInput,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        User sender = userService.getAuthenticatedUser();
        Wallet senderWallet = walletService.getWalletById(UUID.fromString(transactionInput.getWalletId()));
        User recipient = userService.getUserByID(transactionInput.getRecipientId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", sender);
            model.addAttribute("recipient", recipient);
            model.addAttribute("activeWallets", walletService.getActiveWalletsOfUser(sender.getId()));
            return "Transaction-Create-View";
        }

        if (senderWallet.getBalance().compareTo(transactionInput.getAmount()) < 0) {
            model.addAttribute("user", sender);
            model.addAttribute("errorWallet", "Not enough funds in selected wallet.");
            model.addAttribute("recipient", recipient);
            model.addAttribute("activeWallets", walletService.getActiveWalletsOfUser(sender.getId()));
            return "Transaction-Create-View";
        }

        try {
            transactionService.createTransactionMVC(sender, recipient, senderWallet,
                    transactionInput.getAmount(),
                    transactionInput.getDescription());
        } catch (InsufficientFundsException | InvalidUserInputException e) {
            model.addAttribute("user", sender);
            model.addAttribute("errorWallet", e.getMessage());
            model.addAttribute("recipient", recipient);
            model.addAttribute("activeWallets", walletService.getActiveWalletsOfUser(sender.getId()));
            return "Transaction-Create-View";
        }
        model.addAttribute("user", sender);
        redirectAttributes.addFlashAttribute("user", sender);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("transaction", transactionInput);

        return "redirect:/mvc/profile/transactions/success";
    }

    @GetMapping("/success")
    public String transactionSuccess(@ModelAttribute("transaction") TransactionInputMVC transaction,
                                     @ModelAttribute("success") Boolean success,
                                     Model model) {
        if (Boolean.TRUE.equals(success)) {
            User recipient = userService.getUserByID(transaction.getRecipientId());
            Wallet wallet = walletService.getWalletById(UUID.fromString(transaction.getWalletId()));
            model.addAttribute("transaction", transaction);
            model.addAttribute("wallet", wallet.getCurrency().toString());
            model.addAttribute("recipientUsername", recipient.getUsername());
            return "Transaction-Success-View";
        }
        return "Transaction-Success-View";
    }

}
