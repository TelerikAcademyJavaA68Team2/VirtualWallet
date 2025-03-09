package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionInput;
import com.example.virtualwallet.models.dtos.transactions.TransactionOutput;
import com.example.virtualwallet.services.contracts.ExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @PostMapping("/new")
    public ResponseEntity<?> makeTransaction(@Valid @RequestBody ExchangeInput exchangeInput) {
        exchangeService.createExchange(exchangeInput);
        return new ResponseEntity<>("The exchange was successful", HttpStatus.CREATED);

    }
}
