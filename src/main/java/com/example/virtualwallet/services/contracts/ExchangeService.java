package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangeOutput;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;

import java.util.List;

public interface ExchangeService {

    List<ExchangeOutput> filterExchanges(ExchangeFilterOptions filterOptions); //todo

    void createExchange(ExchangeInput input);
}