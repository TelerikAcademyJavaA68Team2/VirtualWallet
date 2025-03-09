package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.exchangeRates.ExchangeRateInput;
import com.example.virtualwallet.services.contracts.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public ResponseEntity<?> getAllExchangeRates() {
        return new ResponseEntity<>(exchangeRateService.getAllExchangeRates(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> editExchange(@RequestBody ExchangeRateInput request) {
        exchangeRateService.updateExchangeRate(request.getFromCurrency(), request.getToCurrency(), request.getExchangeRate());
        return new ResponseEntity<>("Exchange Rate updated successfully!", HttpStatus.OK);
    }

}
