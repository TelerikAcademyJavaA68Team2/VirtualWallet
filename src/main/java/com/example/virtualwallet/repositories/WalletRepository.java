package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Query("SELECT w FROM Wallet w " +
            "JOIN FETCH w.owner u " +
            "WHERE u.username = :username AND w.currency = :currency")
    Optional<Wallet> findByUsernameAndCurrency(@Param("username") String username, @Param("currency") Currency currency);

    @Query("SELECT w FROM Wallet w " +
            "WHERE w.owner.id = :userId AND w.isDeleted = false")
    List<Wallet> findActiveWalletsByUserId(@Param("userId") UUID userId);


    @Query(value =
            """
                    SELECT\s
                        t.id AS id,
                        'transaction' AS activity,
                        t.amount as amount,
                        NULL as toAmount,
                        t.currency AS currency,
                        NULL AS fromCurrency,
                        NULL AS toCurrency,
                        t.sender_username AS senderUsername,
                        t.recipient_username AS recipientUsername,
                        su.photo AS senderPhoto,
                        ru.photo AS recipientPhoto,
                        NULL AS status,
                        t.date as date
                    FROM transaction t
                    JOIN wallet sw ON t.sender_wallet_id = sw.id
                    JOIN user su ON sw.owner_id = su.id
                    JOIN wallet rw ON t.recipient_wallet_id = rw.id
                    JOIN user ru ON rw.owner_id = ru.id
                    WHERE sw.id = :walletId OR rw.id = :walletId
                    
                    UNION ALL
                    
                    SELECT\s
                        tf.id AS id,
                        'transfer' AS activity,
                        tf.amount as amount,
                        NULL as toAmount,
                        tf.currency AS currency,
                        NULL AS fromCurrency,
                        NULL AS toCurrency,
                        NULL AS senderUsername,
                        tf.recipient_username AS recipientUsername,
                        NULL AS senderPhoto,
                        NULL AS recipientPhoto,
                        tf.status AS status,
                        tf.date as date
                    FROM transfer tf
                    JOIN wallet w ON tf.wallet_id = w.id
                    WHERE tf.wallet_id = :walletId
                    
                    UNION ALL
                    
                    SELECT\s
                        e.id AS id,
                        'exchange' AS activity,
                        e.amount as amount,
                        e.to_amount as toAmount,
                        NULL AS currency,
                        e.from_currency AS fromCurrency,
                        e.to_currency AS toCurrency,
                        NULL AS senderUsername,
                        NULL AS recipientUsername,
                        NULL AS senderPhoto,
                        NULL AS recipientPhoto,
                        NULL AS status,
                        e.date as date
                    FROM exchange e
                    JOIN wallet fw ON e.from_wallet_id = fw.id
                    JOIN wallet tw ON e.to_wallet_id = tw.id
                    WHERE fw.id = :walletId OR tw.id = :walletId
                    
                    ORDER BY date DESC""",
            countQuery = """
                    SELECT COUNT(*) FROM (
                       SELECT t.id
                    FROM transaction t
                    JOIN wallet sw ON t.sender_wallet_id = sw.id
                    JOIN wallet rw ON t.recipient_wallet_id = rw.id
                    WHERE sw.id = :walletId OR rw.id = :walletId
                    
                    UNION ALL
                    
                    SELECT\s
                        tf.id
                    FROM transfer tf
                    JOIN wallet w ON tf.wallet_id = w.id
                    WHERE tf.wallet_id = :walletId
                    
                    UNION ALL
                    
                    SELECT e.id
                    FROM exchange e
                    JOIN wallet fw ON e.from_wallet_id = fw.id
                    JOIN wallet tw ON e.to_wallet_id = tw.id
                    WHERE fw.id = :walletId OR tw.id = :walletId
                    ) AS total
                    """,
            nativeQuery = true
    )
    Page<Object[]> findWalletHistory(@Param("walletId") UUID walletId, Pageable pageable);

    @Query(value =
            """
                    SELECT\s
                        t.id AS id,
                        'transaction' AS activity,
                        t.amount as amount,
                        NULL as toAmount,
                        t.currency AS currency,
                        NULL AS fromCurrency,
                        NULL AS toCurrency,
                        t.sender_username AS senderUsername,
                        t.recipient_username AS recipientUsername,
                        su.photo as senderPhoto,
                        ru.photo as recipientPhoto,
                        NULL AS status,
                        t.date as date
                    FROM transaction t
                    JOIN user su ON t.sender_username = su.username
                    JOIN user ru ON t.recipient_username = ru.username
                    WHERE t.sender_username = :userUsername OR t.recipient_username = :userUsername
                    
                    UNION ALL
                    
                    SELECT\s
                        tf.id AS id,
                        'transfer' AS activity,
                        tf.amount as amount,
                        NULL as toAmount,
                        tf.currency AS currency,
                        NULL AS fromCurrency,
                        NULL AS toCurrency,
                        NULL AS senderUsername,
                        tf.recipient_username AS recipientUsername,
                        NULL as senderPhoto,
                        NULL as recipientPhoto,
                        tf.status AS status,
                        tf.date as date
                    FROM transfer tf
                    WHERE tf.recipient_username = :userUsername
                    
                    UNION ALL
                    
                    SELECT\s
                        e.id AS id,
                        'exchange' AS activity,
                        e.amount as amount,
                        e.to_amount as toAmount,
                        NULL AS currency,
                        e.from_currency AS fromCurrency,
                        e.to_currency AS toCurrency,
                        NULL AS senderUsername,
                        NULL AS recipientUsername,
                        NULL as senderPhoto,
                        NULL as recipientPhoto,
                        NULL AS status,
                        e.date as date
                    FROM exchange e
                    WHERE e.recipient_username = :userUsername
                    
                    ORDER BY date DESC""",
            countQuery = """
                    SELECT COUNT(*) FROM (
                       SELECT t.id
                    FROM transaction t
                    WHERE t.sender_username = :userUsername OR t.recipient_username = :userUsername
                    
                    UNION ALL
                    
                    SELECT\s
                        tf.id
                    FROM transfer tf
                    WHERE tf.recipient_username = :userUsername
                    
                    UNION ALL
                    
                    SELECT e.id
                    FROM exchange e
                    WHERE e.recipient_username = :userUsername
                    ) AS total
                    """,
            nativeQuery = true
    )
    Page<Object[]> findUserWalletHistory(@Param("userUsername") String username, Pageable pageable);


    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wallet w " +
            "WHERE w.owner.id = :userId " +
            "AND w.currency = :currency " +
            "AND w.isDeleted = false")
    boolean checkIfUserHasActiveWalletWithCurrency(@Param("userId") UUID userId, @Param("currency") Currency currency);


    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wallet w " +
            "WHERE w.owner.id = :userId " +
            "AND w.id = :walletId " +
            "AND w.isDeleted = false")
    boolean checkIfUserHasActiveWalletWithId(@Param("userId") UUID userId, @Param("walletId") UUID walletId);

}