package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("{walletId}")
    public ResponseEntity<?> getWalletHistory(@PathVariable UUID walletId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(walletService.getWalletPageById(walletId, page, size), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<?> getWalletHistory(@RequestParam String currency) {
        walletService.createAuthenticatedUserWalletWalletByCurrency(currency);
        return new ResponseEntity<>(currency + " Wallet created successfully!", HttpStatus.OK);
    }
}
