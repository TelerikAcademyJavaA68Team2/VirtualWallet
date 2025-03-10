package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import com.example.virtualwallet.repositories.TransferRepository;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletServiceJPA;
import com.example.virtualwallet.services.specifications.TransferSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static com.example.virtualwallet.helpers.ModelMapper.transferToFullTransferInfoOutput;
import static com.example.virtualwallet.helpers.ValidationHelpers.validateAndConvertCurrency;

@RequiredArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    public static final String NOT_CARD_OWNER = "You do not own this card.";

    private final TransferRepository transferRepository;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final CardService cardService;
    private final WalletServiceJPA walletServiceJPA;

    @Transactional
    public FullTransferInfoOutput processTransfer(TransferInput transferInput) {
        User user = userService.getAuthenticatedUser();

        Card card = cardService.getCardById(transferInput.getCardId());

        Currency currency = validateAndConvertCurrency(transferInput.getCurrency());

        if (!card.getOwner().equals(user)) {
            throw new UnauthorizedAccessException(NOT_CARD_OWNER);
        }

        Wallet wallet = walletServiceJPA.getOrCreateWalletByUsernameAndCurrency(user.getUsername(),
                currency);

        TransactionStatus transferStatus = callMockWithdrawApi();

        Transfer transfer = new Transfer();
        transfer.setCard(card);
        transfer.setWallet(wallet);
        transfer.setAmount(transferInput.getAmount());
        transfer.setCurrency(currency);
        transfer.setStatus(transferStatus);
        transfer.setRecipientUsername(user.getUsername());

        if (transfer.getStatus() == TransactionStatus.APPROVED) {
            wallet.setBalance(wallet.getBalance().add(transferInput.getAmount()));
            walletServiceJPA.update(wallet);
        }
        Transfer transferToSave = transferRepository.save(transfer);

        return transferToFullTransferInfoOutput(transferToSave);
    }

    @Override
    public FullTransferInfoOutput getTransferById(UUID id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transfer", id));

        return transferToFullTransferInfoOutput(transfer);
    }

    @Override
    public List<TransferOutput> filterTransfers(TransferFilterOptions filterOptions) {
        Specification<Transfer> spec = TransferSpecification.buildTransferSpecification(filterOptions);

        Sort.Direction direction = filterOptions.getSortOrder().equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, filterOptions.getSortBy());
        Pageable pageable = PageRequest.of(filterOptions.getPage(), filterOptions.getSize(), sort);

        Page<Transfer> pageResult = transferRepository.findAll(spec, pageable);
        return pageResult.stream()
                .map(ModelMapper::transferToTransferOutput)
                .toList();
    }

    private TransactionStatus callMockWithdrawApi() {
        String url = "http://localhost:8080/api/profile/transfers/withdraw";

        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(response.getBody()) ? TransactionStatus.APPROVED : TransactionStatus.DECLINED;
        } catch (Exception e) {
            throw new RuntimeException("Mock Withdraw API error: " + e.getMessage());
        }
    }
}