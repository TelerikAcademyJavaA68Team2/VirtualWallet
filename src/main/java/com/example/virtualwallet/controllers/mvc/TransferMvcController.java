package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.models.dtos.transfer.FullTransferInfoOutput;
import com.example.virtualwallet.services.contracts.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mvc/profile/transfers")
public class TransferMvcController {

    private final TransferService transferService;

    @GetMapping("/{id}")
    public String getSingleTransferView(Model model, @PathVariable UUID id) {
        FullTransferInfoOutput transferOutput = transferService.getTransferById(id);
        model.addAttribute("transfer", transferOutput);
        return "Transfer-View";
    }
}

