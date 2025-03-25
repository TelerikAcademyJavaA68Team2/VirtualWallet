package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransfersPage;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransferStatus;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
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

import static com.example.virtualwallet.helpers.ValidationHelpers.*;
import static com.example.virtualwallet.helpers.ValidationHelpers.validateDateFromUrl;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mvc/profile/transfers")
public class TransferMvcController {

    private final TransferService transferService;
    private final UserService userService;


    @GetMapping("/{id}")
    public String getSingleTransferView(Model model, @PathVariable UUID id) {
        FullTransferInfoOutput transferOutput = transferService.getTransferById(id);
        String recipientId = userService.findUserByUsernameOrEmailOrPhoneNumber(transferOutput.getRecipientUsername()).getId().toString();

        model.addAttribute("personalRequest", true);
        model.addAttribute("recipientId", recipientId);
        model.addAttribute("transfer", transferOutput);
        return "Transfer-View";
    }


    @GetMapping
    public String getTransfersWithFilter(
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
                userService.getAuthenticatedUser().getUsername(),
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

        TransferStatus enumStatus = sanitizeTransferStatus(status).orElse(null);
        String sanitizedStatus = enumStatus == null ? null : enumStatus.toString();
        String sanitizedCurrency = sanitizeStringCurrency(currency).orElse(null);

        model.addAttribute("fromDate",fromDateToDisplay);
        model.addAttribute("toDate",toDateToDisplay);
        model.addAttribute("minAmount",minAmount);
        model.addAttribute("maxAmount",maxAmount);
        model.addAttribute("currency",sanitizedCurrency);
        model.addAttribute("status",sanitizedStatus);
        model.addAttribute("sortBy",sortBy);
        model.addAttribute("sortOrder",sortOrder);
        model.addAttribute("page",page);
        model.addAttribute("size",size);

        model.addAttribute("currencies", currencies);
        model.addAttribute("transfersPage", result);
        return "User-Transfers-View";
    }
}

