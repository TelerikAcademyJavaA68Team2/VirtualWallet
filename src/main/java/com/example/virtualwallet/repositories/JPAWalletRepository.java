package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.wallet.ActivityOutput;
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
public interface JPAWalletRepository extends JpaRepository<Wallet, UUID> {

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
                        'TRANSACTION' AS activity,
                        t.amount as amount,
                        NULL as toAmount,
                        t.currency AS currency,
                        NULL AS fromCurrency,
                        NULL AS toCurrency,
                        t.sender_username AS senderUsername,
                        t.recipient_username AS recipientUsername,
                        NULL AS status,
                        t.date as date
                    FROM Transaction t
                    JOIN Wallet sw ON t.sender_wallet_id = sw.id
                    JOIN Wallet rw ON t.recipient_wallet_id = rw.id
                    WHERE sw.id = :walletId OR rw.id = :walletId
                    
                    UNION ALL
                    
                    SELECT\s
                        tf.id AS id,
                        'ACCOUNT FUNDING' AS activity,
                        tf.amount as amount,
                        NULL as toAmount,
                        tf.currency AS currency,
                        NULL AS fromCurrency,
                        NULL AS toCurrency,
                        NULL AS senderUsername,
                        tf.recipient_username AS recipientUsername,
                        tf.status AS status,
                        tf.date as date
                    FROM Transfer tf
                    JOIN wallet w ON tf.wallet_id = w.id
                    WHERE tf.wallet_id = :walletId
                    
                    UNION ALL
                    
                    SELECT\s
                        e.id AS id,
                        'EXCHANGE' AS activity,
                        e.amount as amount,
                        e.to_amount as toAmount,
                        NULL AS currency,
                        e.from_currency AS fromCurrency,
                        e.to_currency AS toCurrency,
                        NULL AS senderUsername,
                        NULL AS recipientUsername,
                        NULL AS status,
                        e.date as date
                    FROM Exchange e
                    JOIN Wallet fw ON e.from_wallet_id = fw.id
                    JOIN Wallet tw ON e.to_wallet_id = tw.id
                    WHERE fw.id = :walletId OR tw.id = :walletId
                    
                    ORDER BY date DESC""",
            countQuery = """
                    SELECT COUNT(*) FROM (
                       SELECT t.id
                    FROM Transaction t
                    JOIN Wallet sw ON t.sender_wallet_id = sw.id
                    JOIN Wallet rw ON t.recipient_wallet_id = rw.id
                    WHERE sw.id = :walletId OR rw.id = :walletId
                    
                    UNION ALL
                    
                    SELECT\s
                        tf.id
                    FROM Transfer tf
                    JOIN wallet w ON tf.wallet_id = w.id
                    WHERE tf.wallet_id = :walletId
                    
                    UNION ALL
                    
                    SELECT e.id
                    FROM Exchange e
                    JOIN Wallet fw ON e.from_wallet_id = fw.id
                    JOIN Wallet tw ON e.to_wallet_id = tw.id
                    WHERE fw.id = :walletId OR tw.id = :walletId
                    ) AS total
                    """,
            nativeQuery = true
    )
    Page<Object[]> findWalletHistory(@Param("walletId") UUID walletId, Pageable pageable);

    @Query(value =
            "SELECT " +
                    " t.id," +
                    " 'TRANSACTION'," +
                    " t.amount, " +
                    " NULL," +
                    " t.currency, " +
                    " NULL, " +
                    " NULL, " +
                    " t.sender_username, " +
                    " t.recipient_username, " +
                    " NULL," +
                    " t.date " +
                    "FROM virtual_wallet.transaction t " +
                    "JOIN virtual_wallet.wallet sw ON t.sender_wallet_id = sw.id " +
                    "JOIN virtual_wallet.wallet rw ON t.recipient_wallet_id = rw.id " +
                    "WHERE sw.id = :walletId OR rw.id = :walletId " +

                    "UNION ALL" +

                    " SELECT " +
                    " tr.id," +
                    " 'WITHDRAW'," +
                    " tr.amount," +
                    " NULL, " +
                    " tr.currency, " +
                    " NULL," +
                    " NULL, " +
                    " NULL," +
                    " tr.recipient_username," +
                    " tr.status," +
                    " tr.date " +
                    "FROM virtual_wallet.transfer tr " +
                    "JOIN virtual_wallet.wallet w ON tr.wallet_id = w.id " +
                    "WHERE tr.wallet_id = :walletId " +

                    "UNION ALL" +

                    " SELECT e.id, " +
                    " 'EXCHANGE'," +
                    " e.amount," +
                    " e.to_amount," +
                    " NULL, " +
                    " e.from_currency, " +
                    " e.to_currency," +
                    " NULL, " +
                    " e.recipient_username," +
                    " NULL," +
                    " e.date " +
                    "FROM virtual_wallet.exchange e " +
                    "JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
                    "JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
                    "WHERE fw.id = :walletId OR tw.id = :walletId " +

                    "ORDER BY date DESC",
            nativeQuery = true)
    List<ActivityOutput> findWalletHistory2(@Param("walletId") UUID walletId);
/*
    @Query(value = """
            
            SELECT NEW com.example.virtualwallet.models.dtos.wallet.ActivityOutput(
                t.id,
                'TRANSACTION',
                t.amount,
                NULL,
                t.currency,
                NULL,
                NULL,
                t.sender_username,
                t.recipient_username,
                NULL,
                t.date)
            FROM Transaction t
            JOIN Wallet sw ON t.sender_wallet_id = sw.id
            JOIN Wallet rw ON t.recipient_wallet_id = rw.id
            WHERE sw.id = :walletId OR rw.id = :walletId
            
            UNION ALL
            
            SELECT NEW com.example.virtualwallet.models.dtos.wallet.ActivityOutput(
                tf.id,
                'FUNDING',
                tf.amount,
                NULL,
                tf.currency,
                NULL,
                NULL,
                NULL,
                tf.recipient_username,
                tf.status,
                tf.date)
            FROM Transfer tf
            JOIN Wallet w ON tf.wallet_id = w.id
            WHERE tf.wallet_id = :walletId
            
            UNION ALL
            
            SELECT NEW com.example.virtualwallet.models.dtos.wallet.ActivityOutput(
                e.id,
                'EXCHANGE',
                e.amount,
                e.to_amount,
                NULL,
                e.from_currency,
                e.to_currency,
                NULL,
                e.recipient_username,
                NULL,
                e.date)
            FROM Exchange e
            JOIN Wallet fw ON e.from_wallet_id = fw.id
            JOIN Wallet tw ON e.to_wallet_id = tw.id
            WHERE fw.id = :walletId OR tw.id = :walletId
            ORDER BY date DESC
            """,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT t.id FROM Transaction t WHERE t.sender_wallet_id = :walletId OR t.recipient_wallet_id = :walletId
                        UNION ALL
                        SELECT tf.id FROM Transfer tf WHERE tf.wallet_id = :walletId
                        UNION ALL
                        SELECT e.id FROM Exchange e WHERE e.from_wallet_id = :walletId OR e.to_wallet_id = :walletId
                    ) AS total""",
            nativeQuery = true
    )
    Page<ActivityOutput> findWalletHistory(
            @Param("walletId") UUID walletId,
            Pageable pageable
    );*/

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wallet w " +
            "WHERE w.owner.id = :userId " +
            "AND w.currency = :currency " +
            "AND w.isDeleted = false")
    boolean checkIfUserHasActiveWalletWithCurrency(@Param("userId") UUID userId, @Param("currency") Currency currency);


    @Query(value = """
            
            SELECT
                t.id AS id,
                'TRANSACTION' AS activity,
                t.amount AS amount,
                NULL AS toAmount,
                t.currency AS currency,
                NULL AS fromCurrency,
                NULL AS toCurrency,
                t.sender_username AS senderUsername,
                t.recipient_username AS recipientUsername,
                NULL AS status,
                t.date AS date
            FROM Transaction t
            JOIN Wallet sw ON t.sender_wallet_id = sw.id
            JOIN Wallet rw ON t.recipient_wallet_id = rw.id
            WHERE sw.id = :walletId OR rw.id = :walletId
            
            UNION ALL
            
            SELECT
                tf.id AS id,
                'FUNDING' AS activity,
                tf.amount AS amount,
                NULL AS toAmount,
                tf.currency AS currency,
                NULL AS fromCurrency,
                NULL AS toCurrency,
                NULL AS senderUsername,
                tf.recipient_username AS recipientUsername,
                tf.status AS status,
                tf.date AS date
            FROM Transfer tf
            JOIN Wallet w ON tf.wallet_id = w.id
            WHERE tf.wallet_id = :walletId
            
            UNION ALL
            
            SELECT
                e.id AS id,
                'EXCHANGE' AS activity,
                e.amount AS amount,
                e.to_amount AS toAmount,
                NULL AS currency,
                e.from_currency AS fromCurrency,
                e.to_currency AS toCurrency,
                NULL AS senderUsername,
                e.recipient_username AS recipientUsername,
                NULL AS status,
                e.date AS date
            FROM Exchange e
            JOIN Wallet fw ON e.from_wallet_id = fw.id
            JOIN Wallet tw ON e.to_wallet_id = tw.id
            WHERE fw.id = :walletId OR tw.id = :walletId
            ORDER BY date DESC
            """,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT t.id FROM Transaction t WHERE t.sender_wallet_id = :walletId OR t.recipient_wallet_id = :walletId
                        UNION ALL
                        SELECT tf.id FROM Transfer tf WHERE tf.wallet_id = :walletId
                        UNION ALL
                        SELECT e.id FROM Exchange e WHERE e.from_wallet_id = :walletId OR e.to_wallet_id = :walletId
                    ) AS total""",
            nativeQuery = true
    )
    Page<ActivityOutput> findWalletHistory2(
            @Param("walletId") UUID walletId,
            Pageable pageable
    );
}