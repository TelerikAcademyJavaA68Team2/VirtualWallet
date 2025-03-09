package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, UUID>, JpaSpecificationExecutor<Exchange> {
}
