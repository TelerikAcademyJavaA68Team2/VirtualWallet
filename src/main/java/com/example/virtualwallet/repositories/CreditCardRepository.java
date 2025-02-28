package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface CreditCardRepository extends JpaRepository<CreditCard, UUID> {
   /* @Query("""
            SELECT DISTINCT c FROM CreditCard c
            LEFT JOIN FETCH c.user us
            WHERE us.id = :userId AND c.expirationDate > current time
            """)
    Set<CreditCard> findAllCreditCardsByUserId(@Param("userId") UUID userId);*/

}
