package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.dtos.transfer.TransferInputMVC;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;

import java.util.List;
import java.util.UUID;

public interface TransferService {

    FullTransferInfoOutput processTransfer(TransferInput transferInput);

    FullTransferInfoOutput processTransfer(TransferInputMVC transferInput);

    FullTransferInfoOutput getTransferById(UUID id);

    List<TransferOutput> filterTransfers(TransferFilterOptions transferFilterOptions);
}