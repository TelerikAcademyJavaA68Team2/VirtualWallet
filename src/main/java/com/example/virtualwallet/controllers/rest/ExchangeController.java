package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangeOutput;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import com.example.virtualwallet.services.contracts.ExchangeService;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getExchangesAndFilter(@RequestParam(required = false) String fromDate,
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
                                                   @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
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

        List<ExchangeOutput> result = exchangeService.filterExchanges(filterOptions);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public FullExchangeInfoOutput getFullExchangeInfoById(@PathVariable UUID id) {
        return exchangeService.getExchangeById(id);
    }

    @PostMapping("/new")
    public ResponseEntity<?> makeTransaction(@Valid @RequestBody ExchangeInput exchangeInput) {
        exchangeService.createExchange(exchangeInput);
        return new ResponseEntity<>("The exchange was successful", HttpStatus.CREATED);

    }
}
