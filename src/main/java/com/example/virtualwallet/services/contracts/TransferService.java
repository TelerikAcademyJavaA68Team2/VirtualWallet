package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.dtos.TransferInput;
import com.example.virtualwallet.models.dtos.TransferOutput;

public interface TransferService {

   TransferOutput processTransfer(TransferInput transferInput);

}
