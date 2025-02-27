package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditCardRepository extends JpaRepository<CreditCard, UUID> {

}
