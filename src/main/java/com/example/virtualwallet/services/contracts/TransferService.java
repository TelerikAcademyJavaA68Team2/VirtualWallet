package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.dtos.transfer.*;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;

import java.util.UUID;

public interface TransferService {

    FullTransferInfoOutput processTransfer(TransferInput transferInput);

    FullTransferInfoOutput processTransfer(TransferInputMVC transferInput);

    FullTransferInfoOutput getTransferById(UUID id);

    TransfersPage filterTransfers(TransferFilterOptions transferFilterOptions);
}