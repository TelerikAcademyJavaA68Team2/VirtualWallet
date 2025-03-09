package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.dtos.exchange.ExchangeInput;
import com.example.virtualwallet.models.dtos.exchange.ExchangeOutput;

import java.util.List;

public interface ExchangeService {

    List<ExchangeOutput> filterExchanges(); //todo

    void createExchange(ExchangeInput input);
}