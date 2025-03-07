package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.TransferInput;
import com.example.virtualwallet.models.dtos.TransferOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;
import com.example.virtualwallet.repositories.TransferRepository;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.example.virtualwallet.helpers.ValidationHelpers.validateAndConvertCurrency;

@RequiredArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    public static final String NOT_CARD_OWNER = "You do not own this card.";

    private final TransferRepository transferRepository;
    private final WalletService walletService;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final CardService cardService;

    @Transactional
    public TransferOutput processTransfer(TransferInput transferInput) {
        User user = userService.getAuthenticatedUser();

        Card card = cardService.getCardById(transferInput.getCardId());

        Currency currency = validateAndConvertCurrency(transferInput.getCurrency());

        if (!card.getOwner().equals(user)) {
            throw new UnauthorizedAccessException(NOT_CARD_OWNER);
        }

        Wallet wallet = walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(),
                currency);

        TransactionStatus transferStatus = callMockWithdrawApi();

        Transfer transfer = ModelMapper.createTransferFromTransferInput(transferInput, card,
                wallet, transferStatus, currency);

        transferRepository.save(transfer);

        if (transfer.getStatus() == TransactionStatus.APPROVED) {
            wallet.setBalance(wallet.getBalance().add(transferInput.getAmount()));
            walletService.update(wallet, user);
        }

        return ModelMapper.transferToTransferOutput(transfer);
    }

    @Override
    public List<TransferOutput> findAllTransfersByUserId(User user) {
        return transferRepository.findTransferByWallet_Owner_Id((user.getId()), Sort.by(Sort.Direction.DESC, "date"))
                .stream().map(ModelMapper::transferToTransferOutput).toList();
    }

    private TransactionStatus callMockWithdrawApi() {
        String url = "http://localhost:8080/api/transfers/withdraw";

        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(response.getBody()) ? TransactionStatus.APPROVED : TransactionStatus.DECLINED;
        } catch (Exception e) {
            throw new RuntimeException("Mock Withdraw API error: " + e.getMessage());
        }
    }
}

