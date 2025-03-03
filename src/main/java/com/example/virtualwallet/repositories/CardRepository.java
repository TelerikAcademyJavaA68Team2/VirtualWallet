package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> getAllByOwner_Id(UUID ownerId);

    @Query("SELECT COUNT(c) from Card c where c.owner.id = :ownerId")
    Long getTotalNumberOfCardsByOwner_Id(UUID ownerId);

    Card getCardByCardNumber(String cardNumber);

}
