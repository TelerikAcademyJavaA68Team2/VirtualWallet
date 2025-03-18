package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangeOutput;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import com.example.virtualwallet.services.contracts.ExchangeService;
import com.example.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.controllers.rest.AdminRestController.INVALID_PAGE_OR_SIZE_PARAMETERS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/exchanges")
@Tag(name = "Exchange Management", description = "Endpoints for user exchanges")
public class ExchangeController {

    public static final String EXCHANGE_SUCCESSFUL = "The exchange was successful";
    private final ExchangeService exchangeService;
    private final UserService userService;

    @Operation(
            summary = "Retrieve all of user's exchanges with filter options",
            description = "Fetch a list of user's exchanges with " +
                    "filter options, sorting and pagination.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
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
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
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

    @Operation(
            description = "Retrieve an exchange by its ID",
            summary = "Get exchange by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange"),
                    @ApiResponse(responseCode = "404", description = "Exchange not found")
            }
    )
    @GetMapping("/{id}")
    public FullExchangeInfoOutput getFullExchangeInfoById(@PathVariable UUID id) {
        return exchangeService.getExchangeById(id);
    }

    @Operation(
            summary = "Make an exchange",
            description = "Make an exchange from one currency to another",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully made an exchange"),
                    @ApiResponse(responseCode = "400", description = "Invalid input Data"),
                    @ApiResponse(responseCode = "400", description = "Insufficient Funds in your wallet"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
            }
    )
    @PostMapping("/new")
    public ResponseEntity<?> makeExchange(@Valid @RequestBody ExchangeInput exchangeInput) {
        exchangeService.createExchange(exchangeInput);
        return new ResponseEntity<>(EXCHANGE_SUCCESSFUL, HttpStatus.CREATED);

    }
}
