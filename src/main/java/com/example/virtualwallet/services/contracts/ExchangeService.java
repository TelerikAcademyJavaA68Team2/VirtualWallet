package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangePage;
import com.example.virtualwallet.models.dtos.exchange.FullExchangeInfoOutput;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;

import java.util.UUID;

public interface ExchangeService {

    ExchangePage filterExchanges(ExchangeFilterOptions filterOptions);

    void createExchange(ExchangeInput input);

    FullExchangeInfoOutput getExchangeById(UUID id);
}