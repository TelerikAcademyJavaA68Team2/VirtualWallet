package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.dtos.exchange.ExchangePage;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.dtos.transactions.FullTransactionInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransfersPage;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.services.contracts.ExchangeService;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/admin")
public class AdminMvcController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final TransferService transferService;
    private final ExchangeService exchangeService;


    @GetMapping("/transactions")
    public String getUserTransactions(
            @RequestParam(required = false) String specificUser,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient,
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
                specificUser, fromDate, toDate, minAmount, maxAmount, currency,
                sender, recipient, null, description, sortBy, sortOrder, page, size
        );

        List<String> currencies = Arrays.stream(Currency.values()).map((Enum::name)).toList();
        Page<Transaction> transactionsPage = transactionService.filterTransactionsPage(filterOptions);

        int startIndex = page * size;
        model.addAttribute("specificUser", specificUser);
        model.addAttribute("currencies", currencies);
        model.addAttribute("transactions", transactionsPage);
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("fromDate", fromDateToDisplay);
        model.addAttribute("toDate", toDateToDisplay);
        model.addAttribute("minAmount", minAmount);
        model.addAttribute("maxAmount", maxAmount);
        model.addAttribute("currency", currency);
        model.addAttribute("sender", sender);
        model.addAttribute("recipient", recipient);
        model.addAttribute("description", description);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "Admin-Transactions-View";
    }


    @GetMapping("/transfers")
    public String getTransfersWithFilter(
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String status,
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
        TransferFilterOptions filterOptions = new TransferFilterOptions(
                recipient,
                fromDate,
                toDate,
                minAmount,
                maxAmount,
                currency,
                status,
                sortBy, sortOrder, page, size
        );

        TransfersPage result = transferService.filterTransfers(filterOptions);
        List<String> currencies = Arrays.stream(Currency.values()).map((Enum::name)).toList();


        model.addAttribute("recipient", recipient);
        model.addAttribute("fromDate", fromDateToDisplay);
        model.addAttribute("toDate", toDateToDisplay);
        model.addAttribute("minAmount", minAmount);
        model.addAttribute("maxAmount", maxAmount);
        model.addAttribute("currency", currency);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        model.addAttribute("currencies", currencies);
        model.addAttribute("transfersPage", result);
        return "Admin-Transfers-View";
    }


    @GetMapping("/exchanges")
    public String getExchangesAndFilter(
            @RequestParam(required = false) String recipientUsername,
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

        model.addAttribute("recipient", recipientUsername);
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
        return "Admin-Exchanges-View";
    }


    @GetMapping("/users")
    public String filterUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
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
        UserFilterOptions userFilterOptions = new UserFilterOptions(
                username,
                email,
                phoneNumber,
                role,
                status,
                fromDate,
                toDate,
                sortBy,
                sortOrder,
                page,
                size
        );

        UserPageOutput usersPage = userService.filterUsers(userFilterOptions);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("role", role);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDateToDisplay);
        model.addAttribute("toDate", toDateToDisplay);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "Admin-Users-View";
    }


    @GetMapping("/transactions/{id}")
    public String getSingleTransactionView(Model model, @PathVariable UUID id) {
        FullTransactionInfoOutput transactionInfoOutput = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transactionInfoOutput);
        return "Transaction-View";
    }

    @GetMapping("/transfers/{id}")
    public String getSingleTransferView(Model model, @PathVariable UUID id) {
        FullTransferInfoOutput transferOutput = transferService.getTransferById(id);
        model.addAttribute("transfer", transferOutput);
        return "Transfer-View";
    }

    @GetMapping("/exchanges/{id}")
    public String getSingleTransferView(@PathVariable UUID id, Model model) {
        FullExchangeInfoOutput exchange = exchangeService.getExchangeById(id);
        model.addAttribute("exchange", exchange);
        return "Exchange-View";
    }


    @GetMapping("/users/{id}")
    public String getProfile(@PathVariable UUID id, Model model) {
        if (userService.getAuthenticatedUser().getId().equals(id)) {
            return "redirect:/mvc/profile";
        }

        UserProfileOutput profile = userService.getUserProfileById(id);

        model.addAttribute("user", profile);
        return "Admin-User-View";
    }

    @GetMapping("/users/{id}/block")
    public String blockUser(@PathVariable UUID id) {
        try {
            userService.blockUser(id);
        } catch (InvalidUserInputException ignored) {
        }
        return "redirect:/mvc/admin/users/" + id;
    }

    @GetMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable UUID id) {
        try {
            userService.unblockUser(id);
        } catch (InvalidUserInputException ignored) {
        }
        return "redirect:/mvc/admin/users/" + id;
    }

    @GetMapping("/users/{id}/make-admin")
    public String makeAdmin(@PathVariable UUID id) {
        try {
            userService.promoteToAdmin(id);
        } catch (InvalidUserInputException ignored) {
        }
        return "redirect:/mvc/admin/users/" + id;
    }

    @GetMapping("/users/{id}/revoke-admin")
    public String revokeAdmin(@PathVariable UUID id) {
        try {
            userService.demoteToUser(id);
        } catch (InvalidUserInputException ignored) {
        }
        return "redirect:/mvc/admin/users/" + id;
    }


}