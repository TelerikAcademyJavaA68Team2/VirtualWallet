package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.transfer.TransferInput;
import com.example.virtualwallet.models.dtos.transfer.TransferOutput;

import java.util.List;

public interface TransferService {

   TransferOutput processTransfer(TransferInput transferInput);

   List<TransferOutput> findAllTransfersByUserId (User user);

}
