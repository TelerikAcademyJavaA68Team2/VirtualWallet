package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    @Query("SELECT c FROM Card c WHERE c.owner.id = :ownerId AND c.isDeleted = false")
    List<Card> getAllByOwner_Id(@Param("ownerId") UUID ownerId);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.owner.id = :ownerId AND c.isDeleted = false")
    Integer getTotalNumberOfCardsByOwner_Id(@Param("ownerId") UUID ownerId);

    Card getCardByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c WHERE c.id = :cardId AND c.isDeleted = false")
    Optional<Card> findActiveCardById(@Param("cardId") UUID cardId);


    @Query("SELECT t FROM Transfer t WHERE t.card.id = :cardId ORDER BY t.date DESC ")
    List<Transfer> getHistoryById(@Param("cardId") UUID cardId);
}
