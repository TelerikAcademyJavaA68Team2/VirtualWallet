package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    @Query("SELECT c FROM Card c WHERE c.owner.id = :ownerId AND c.isDeleted = false")
    List<Card> getAllByOwner_Id(UUID ownerId);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.owner.id = :ownerId AND c.isDeleted = false")
    Integer getTotalNumberOfCardsByOwner_Id(UUID ownerId);

    Card getCardByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c WHERE c.id = :cardId AND c.isDeleted = false")
    Optional<Card> findById(UUID cardId);

}
