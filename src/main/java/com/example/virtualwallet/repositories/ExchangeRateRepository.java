package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.ExchangeRate;
import com.example.virtualwallet.models.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {
    ExchangeRate findByFromCurrencyAndToCurrency(Currency fromCurrency, Currency toCurrency);
}
