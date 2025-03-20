package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.transactions.FullTransactionInfoOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ValidationHelpers.convertToCustomFormat;
import static com.example.virtualwallet.helpers.ValidationHelpers.requestIsWithInvalidPageOrSize;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mvc/profile/transactions")
public class TransactionMvcController {

    private final UserService userService;
    private final TransactionService transactionService;

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
        model.addAttribute("currency", currency);
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
        model.addAttribute("transaction", transactionInfoOutput);
        return "Transaction-View";
    }
}
