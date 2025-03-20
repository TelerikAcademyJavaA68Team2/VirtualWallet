package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.transfer.*;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.TransactionStatus;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import com.example.virtualwallet.repositories.TransferRepository;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
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

import static com.example.virtualwallet.helpers.ModelMapper.convertToSort;
import static com.example.virtualwallet.helpers.ModelMapper.transferToFullTransferInfoOutput;
import static com.example.virtualwallet.helpers.ValidationHelpers.*;

@RequiredArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    public static final String NOT_CARD_OWNER = "Only card owners can make transfers!";
    public static final String EXPIRED_CARD = "Your card has expired! Please, go to your profile and update your card.";

    private final TransferRepository transferRepository;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final CardService cardService;
    private final WalletService walletService;

    @Transactional
    public FullTransferInfoOutput processTransfer(TransferInput transferInput) {
        User user = userService.getAuthenticatedUser();

        Card card = cardService.getCardById(transferInput.getCardId());

        validateCardIsNotExpired(card.getExpirationDate(), EXPIRED_CARD);

        Currency currency = validateAndConvertCurrency(transferInput.getCurrency());

        validateUserIsCardOwner(card, user, NOT_CARD_OWNER);

        Wallet wallet = walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(),
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
            walletService.update(wallet);
        }
        Transfer transferToSave = transferRepository.save(transfer);

        return transferToFullTransferInfoOutput(transferToSave);
    }

    @Override
    public FullTransferInfoOutput processTransfer(TransferInputMVC transferInput) {
        User user = userService.getAuthenticatedUser();

        Card card = cardService.getCardById(transferInput.getCardId());

        validateCardIsNotExpired(card.getExpirationDate(), EXPIRED_CARD);

        Currency currency = walletService.getWalletById(transferInput.getWalletId()).getCurrency();

        validateUserIsCardOwner(card, user, NOT_CARD_OWNER);

        Wallet wallet = walletService.getOrCreateWalletByUsernameAndCurrency(user.getUsername(),
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
            walletService.update(wallet);
        }
        Transfer transferToSave = transferRepository.save(transfer);

        return transferToFullTransferInfoOutput(transferToSave);
    }

    @Override
    public FullTransferInfoOutput getTransferById(UUID id) {
        User user = userService.getAuthenticatedUser();

        Transfer transfer;
        if (user.getRole().equals(Role.ADMIN)) {
            transfer = transferRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Transfer", id));
        } else {
            transfer = transferRepository.findByIdAndUsername(id, user.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("Transfer", id));
        }
        return transferToFullTransferInfoOutput(transfer);
    }

    @Override
    public TransfersPage filterTransfers(TransferFilterOptions filterOptions) {
        Specification<Transfer> spec = TransferSpecification.buildTransferSpecification(filterOptions);

        Sort sort = convertToSort(filterOptions.getSortBy(), filterOptions.getSortOrder());
        Pageable pageable = PageRequest.of(filterOptions.getPage(), filterOptions.getSize(), sort);

        Page<Transfer> pageResult = transferRepository.findAll(spec, pageable);

        List<TransferOutput> listTransfers = pageResult.stream()
                .map(ModelMapper::transferToTransferOutput)
                .toList();

        TransfersPage transfersPage = new TransfersPage();
        transfersPage.setTransfers(listTransfers);

        transfersPage.setTotalElements(pageResult.getTotalElements());
        transfersPage.setCurrentPage(filterOptions.getPage());
        transfersPage.setTotalPages(pageResult.getTotalPages());
        transfersPage.setPageSize(filterOptions.getSize());
        transfersPage.setHasNextPage(pageResult.hasNext());
        transfersPage.setHasPreviousPage(pageResult.hasPrevious());

        return transfersPage;
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