package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;

import java.util.List;
import java.util.UUID;

public interface TransferService {

   TransferOutput processTransfer(TransferInput transferInput);

   List<TransferOutput> findAllTransfersByUserId (User user);

    List<TransferOutput> findAllTransfersByUserIdWithFilters(UUID userId, TransferFilterOptions transferFilterOptions);
}
