package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<?> getAllWallets() {
        return new ResponseEntity<>(walletService.getActiveWalletsOfAuthenticatedUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createWallet(@RequestParam String currency) {
        walletService.createAuthenticatedUserWalletWalletByCurrency(currency);
        return new ResponseEntity<>(currency + " Wallet created successfully!", HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteWallet(@RequestParam String currency) {
        walletService.softDeleteAuthenticatedUserWalletByCurrency(currency);
        return new ResponseEntity<>(currency + " Wallet deleted successfully!", HttpStatus.OK);
    }

    @GetMapping("/activity")
    public ResponseEntity<?> getWalletHistory(@RequestParam String currency,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {

        return new ResponseEntity<>(walletService.getWalletPageById(currency, page, size), HttpStatus.OK);
    }


}
