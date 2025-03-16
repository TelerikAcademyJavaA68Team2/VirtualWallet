package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.virtualwallet.controllers.rest.AdminRestController.INVALID_PAGE_OR_SIZE_PARAMETERS;
import static com.example.virtualwallet.helpers.ValidationHelpers.requestIsWithInvalidPageOrSize;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/wallets")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<?> getAllWallets(@RequestParam(defaultValue = "BGN") String mainCurrency,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        if (requestIsWithInvalidPageOrSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        return new ResponseEntity<>(walletService.getActiveWalletsOfAuthenticatedUser(mainCurrency, page, size), HttpStatus.OK);
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

    @GetMapping("/{walletId}")
    public ResponseEntity<?> getWalletHistory(@PathVariable UUID walletId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        if (requestIsWithInvalidPageOrSize(page, size)) {
            return ResponseEntity.badRequest().body(INVALID_PAGE_OR_SIZE_PARAMETERS);
        }
        return new ResponseEntity<>(walletService.getWalletPageById(walletId, page, size), HttpStatus.OK);
    }


}
