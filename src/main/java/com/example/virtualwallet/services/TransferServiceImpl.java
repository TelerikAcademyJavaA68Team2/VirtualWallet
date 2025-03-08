package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import com.example.virtualwallet.models.fillterOptions.TransferSpecification;
import com.example.virtualwallet.repositories.TransferRepository;
import com.example.virtualwallet.services.contracts.*;
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
    public TransferOutput processTransfer(TransferInput transferInput) {
        User user = userService.getAuthenticatedUser();

        Card card = cardService.getCardById(transferInput.getCardId());

        Currency currency = validateAndConvertCurrency(transferInput.getCurrency());

        if (!card.getOwner().equals(user)) {
            throw new UnauthorizedAccessException(NOT_CARD_OWNER);
        }

        Wallet wallet = walletServiceJPA.getOrCreateWalletByUsernameAndCurrency(user.getUsername(),
                currency);

        TransactionStatus transferStatus = callMockWithdrawApi();

        Transfer transfer = ModelMapper.createTransferFromTransferInput(transferInput, card,
                wallet, transferStatus, currency);


        if (transfer.getStatus() == TransactionStatus.APPROVED) {
            wallet.setBalance(wallet.getBalance().add(transferInput.getAmount()));
            walletServiceJPA.update(wallet);
        }
        Transfer transferToSave = transferRepository.save(transfer);

        return ModelMapper.transferToTransferOutput(transferToSave, card.getId(), wallet.getId());
    }

    @Override
    public List<TransferOutput> findAllTransfersByUserId(User user) {
        return transferRepository.findTransferByWallet_Owner_Id((user.getId()), Sort.by(Sort.Direction.DESC, "date"))
                .stream().map(ModelMapper::transferToTransferOutput).toList();
    }

    @Override
    public List<TransferOutput> findAllTransfersByUserIdWithFilters(UUID userId, TransferFilterOptions transferFilterOptions) {

            // 1) Build the Specification
            Specification<Transfer> spec =
                    TransferSpecification.buildtransferSpecification(userId, transferFilterOptions);

            // 2) Convert sortBy, sortOrder to a Sort object
            Sort.Direction direction =
                    transferFilterOptions.getSortOrder().equalsIgnoreCase("desc")
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

            //  decide which fields are valid. E.g. "date" or "amount"
            // to avoid letting the user supply something random that breaks your query.
            Sort sort = Sort.by(direction, transferFilterOptions.getSortBy());

            // 3) Build the pageable
            Pageable pageable = PageRequest.of(
                    transferFilterOptions.getPage(),
                    transferFilterOptions.getSize(),
                    sort
            );

            // 4) Fetch from repository
            Page<Transfer> pageResult = transferRepository.findAll(spec, pageable);

            // 5) Map to whatever DTO/Output form you need
            //    Using .map(...) from Spring Data is easy, but you can also .stream()
            return pageResult
                    .stream()
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

