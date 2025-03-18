package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateInput;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/exchange-rate")
@Tag(name = "Exchange Rate Management", description = "Endpoints for exchange rates")

public class ExchangeRateController {

    public static final String UPDATED_SUCCESSFULLY = "Exchange Rate updated successfully!";

    private final ExchangeRateService exchangeRateService;

    @Operation(
            summary = "Retrieve all exchange rates",
            description = "Fetch a list of all exchange rates",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping
    public ResponseEntity<?> getAllExchangeRates() {
        return new ResponseEntity<>(exchangeRateService.getAllExchangeRates(), HttpStatus.OK);
    }

    @Operation(
            description = "Update exchange rate",
            summary = "Update exchange rate",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated card"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid user input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized to update card")
            }
    )
    @PostMapping
    public ResponseEntity<?> editExchange(@RequestBody ExchangeRateInput request) {
        exchangeRateService.updateExchangeRate(request.getFromCurrency(), request.getToCurrency(), request.getExchangeRate());
        return new ResponseEntity<>(UPDATED_SUCCESSFULLY, HttpStatus.OK);
    }

}
