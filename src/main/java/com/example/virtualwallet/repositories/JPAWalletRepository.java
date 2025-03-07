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


    @Query(value = "SELECT * FROM (" + // raboti
            "SELECT t.id AS id, 'TRANSACTION' AS transactionType, t.amount AS amount, t.currency AS currency, " +
            "NULL AS fromCurency, NULL AS toCurency, NULL AS exchangeRate, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = sw.owner_id) AS senderUsername, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = rw.owner_id) AS recipientUsername, " +
            "NULL AS status, t.date AS date " +
            "FROM virtual_wallet.transaction t " +
            "JOIN virtual_wallet.wallet sw ON t.sender_wallet_id = sw.id " +
            "JOIN virtual_wallet.wallet rw ON t.recipient_wallet_id = rw.id " +
            "WHERE sw.id = :walletId OR rw.id = :walletId " +

            "UNION " +

            "SELECT tr.id, 'WITHDRAW', tr.amount, tr.currency, NULL, NULL, NULL, " +
            "NULL, (SELECT u.username FROM virtual_wallet.user u WHERE u.id = w.owner_id), tr.status, tr.date " +
            "FROM virtual_wallet.transfer tr " +
            "JOIN virtual_wallet.wallet w ON tr.wallet_id = w.id " +
            "WHERE tr.wallet_id = :walletId " +

            "UNION " +

            "SELECT e.id, 'EXCHANGE', e.amount, NULL, e.from_currency, e.to_currency, e.exchange_rate, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = fw.owner_id), NULL, NULL, e.date " +
            "FROM virtual_wallet.exchange e " +
            "JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
            "JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
            "WHERE fw.id = :walletId OR tw.id = :walletId " +
            ") AS activity " +
            "ORDER BY activity.date DESC", nativeQuery = true)
    List<ActivityOutput> findActivitiesByWalletId(@Param("walletId") UUID walletId);




    @Query(value =
            "SELECT " +
                    "  id, " +
                    "  transactionType, " +
                    "  amount, " +
                    "  currency, " + // Ensure this is a string (e.g., 'USD')
                    "  fromCurrency, " +
                    "  toCurrency, " +
                    "  exchangeRate, " +
                    "  senderUsername, " +
                    "  recipientUsername, " +
                    "  status, " +
                    "  date " +
                    "FROM (" +
                    "  SELECT " +
                    "    t.id, " +
                    "    'TRANSACTION' AS transactionType, " +
                    "    t.amount, " +
                    "    t.currency, " +
                    "    NULL AS fromCurrency, " +
                    "    NULL AS toCurrency, " +
                    "    NULL AS exchangeRate, " +
                    "    (SELECT u.username FROM virtual_wallet.user u WHERE u.id = sw.owner_id) AS senderUsername, " +
                    "    (SELECT u.username FROM virtual_wallet.user u WHERE u.id = rw.owner_id) AS recipientUsername, " +
                    "    NULL AS status, " +
                    "    t.date " +
                    "  FROM virtual_wallet.transaction t " +
                    "  JOIN virtual_wallet.wallet sw ON t.sender_wallet_id = sw.id " +
                    "  JOIN virtual_wallet.wallet rw ON t.recipient_wallet_id = rw.id " +
                    "  WHERE sw.id = :walletId OR rw.id = :walletId " +
                    "  UNION " +
                    "  SELECT " +
                    "    tr.id, " +
                    "    'WITHDRAW', " +
                    "    tr.amount, " +
                    "    tr.currency, " +
                    "    NULL, " +
                    "    NULL, " +
                    "    NULL, " +
                    "    NULL, " +
                    "    (SELECT u.username FROM virtual_wallet.user u WHERE u.id = w.owner_id), " +
                    "    tr.status, " +
                    "    tr.date " +
                    "  FROM virtual_wallet.transfer tr " +
                    "  JOIN virtual_wallet.wallet w ON tr.wallet_id = w.id " +
                    "  WHERE tr.wallet_id = :walletId " +
                    "  UNION " +
                    "  SELECT " +
                    "    e.id, " +
                    "    'EXCHANGE', " +
                    "    e.amount, " +
                    "    NULL, " +
                    "    e.from_currency, " +
                    "    e.to_currency, " +
                    "    e.exchange_rate, " +
                    "    (SELECT u.username FROM virtual_wallet.user u WHERE u.id = fw.owner_id), " +
                    "    NULL, " +
                    "    NULL, " +
                    "    e.date " +
                    "  FROM virtual_wallet.exchange e " +
                    "  JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
                    "  JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
                    "  WHERE fw.id = :walletId OR tw.id = :walletId " +
                    ") AS activity " +
                    "ORDER BY date DESC",
            nativeQuery = true
    )
    List<ActivityOutput> testPagination(@Param("walletId") UUID walletId, Pageable pageable);


    @Query(value = "SELECT* FROM (" + // testvane
            "SELECT t.id AS id, 'TRANSACTION' AS transactionType, t.amount AS amount, t.currency AS currency, " +
            "NULL AS fromCurrency, NULL AS toCurrency, NULL AS exchangeRate, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = sw.owner_id) AS senderUsername, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = rw.owner_id) AS recipientUsername, " +
            "NULL AS status, t.date AS date " +
            "FROM virtual_wallet.transaction t " +
            "JOIN virtual_wallet.wallet sw ON t.sender_wallet_id = sw.id " +
            "JOIN virtual_wallet.wallet rw ON t.recipient_wallet_id = rw.id " +
            "WHERE sw.id = :walletId OR rw.id = :walletId " +

            "UNION " +

            "SELECT tr.id, 'WITHDRAW', tr.amount, tr.currency, NULL, NULL, NULL, " +
            "NULL, (SELECT u.username FROM virtual_wallet.user u WHERE u.id = w.owner_id), tr.status, tr.date " +
            "FROM virtual_wallet.transfer tr " +
            "JOIN virtual_wallet.wallet w ON tr.wallet_id = w.id " +
            "WHERE tr.wallet_id = :walletId " +

            "UNION " +

            "SELECT e.id, 'EXCHANGE', e.amount, NULL, e.from_currency, e.to_currency, e.exchange_rate, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = fw.owner_id), NULL, NULL, e.date " +
            "FROM virtual_wallet.exchange e " +
            "JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
            "JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
            "WHERE fw.id = :walletId OR tw.id = :walletId " +
            ") AS activity " +
            "ORDER BY activity.date DESC", nativeQuery = true)
    List<ActivityOutput> testPagination2(@Param("walletId") UUID walletId,Pageable pageable);






    @Query(value = "SELECT * FROM (" +
            "SELECT t.id AS id, 'TRANSACTION' AS type, t.amount AS amount, t.currency AS currency, " +
            "NULL AS from_currency, NULL AS to_currency, NULL AS exchange_rate, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = sw.owner_id) AS sender_username, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = rw.owner_id) AS recipient_username, " +
            "NULL AS status, t.date AS date " +
            "FROM virtual_wallet.transaction t " +
            "JOIN virtual_wallet.wallet sw ON t.sender_wallet_id = sw.id " +
            "JOIN virtual_wallet.wallet rw ON t.recipient_wallet_id = rw.id " +
            "WHERE sw.id = :walletId OR rw.id = :walletId " +

            "UNION " +

            "SELECT tr.id, 'WITHDRAW', tr.amount, tr.currency, NULL, NULL, NULL, " +
            "NULL, (SELECT u.username FROM virtual_wallet.user u WHERE u.id = w.owner_id), tr.status, tr.date " +
            "FROM virtual_wallet.transfer tr " +
            "JOIN virtual_wallet.wallet w ON tr.wallet_id = w.id " +
            "WHERE tr.wallet_id = :walletId " +

            "UNION " +

            "   SELECT e.id, 'EXCHANGE', e.amount, NULL, e.from_currency, e.to_currency, e.exchange_rate, " +
            "   (SELECT u.username FROM virtual_wallet.user u WHERE u.id = fw.owner_id), NULL, NULL, e.date " +
            "   FROM virtual_wallet.exchange e " +
            "   JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
            "   JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
            "   WHERE fw.id = :walletId OR tw.id = :walletId " +
            ") AS activity " +
            "ORDER BY date DESC " +
            "LIMIT ?#{#pageable.offset}, ?#{#pageable.pageSize}",

            countQuery = "SELECT COUNT(*) FROM (" +
                    "SELECT t.id FROM virtual_wallet.transaction t " +
                    "JOIN virtual_wallet.wallet sw ON t.sender_wallet_id = sw.id " +
                    "JOIN virtual_wallet.wallet rw ON t.recipient_wallet_id = rw.id " +
                    "WHERE sw.id = ?1 OR rw.id = ?1 " +

                    "   UNION " +

                    "   SELECT tr.id FROM virtual_wallet.transfer tr " +
                    "   JOIN virtual_wallet.wallet w ON tr.wallet_id = w.id " +
                    "   WHERE tr.wallet_id = ?1 " +

                    "   UNION " +

                    "SELECT e.id FROM virtual_wallet.exchange e " +
                    "JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
                    "JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
                    "WHERE fw.id = ?1 OR tw.id = ?1" +
                    ") AS activity",
            nativeQuery = true
    )
    Page<ActivityOutput> getWalletActivities(
            @Param("walletId") UUID walletId,
            Pageable pageable
    );

/*
    @Query(value =
            "SELECT * FROM (" +
                    "   SELECT " +
                    "       t.id AS id, " +
                    "       'TRANSACTION' AS type, " +
                    "       t.amount AS amount, " +
                    "       t.currency AS currency, " +
                    "       NULL AS from_currency, " +
                    "       NULL AS to_currency, " +
                    "       NULL AS exchange_rate, " +
                    "       (SELECT u.username FROM virtual_wallet.user u WHERE u.id = sw.owner_id) AS sender_username, " +
                    "       (SELECT u.username FROM virtual_wallet.user u WHERE u.id = rw.owner_id) AS recipient_username, " +
                    "       NULL AS status, " +
                    "       t.date AS date " +
                    "   FROM virtual_wallet.transaction t " +
                    "   JOIN virtual_wallet.wallet sw ON t.sender_wallet_id = sw.id " +
                    "   JOIN virtual_wallet.wallet rw ON t.recipient_wallet_id = rw.id " +
                    "   WHERE sw.id = :walletId OR rw.id = :walletId " +

                    "   UNION " +

                    "   SELECT " +
                    "       tr.id, " +
                    "       'WITHDRAW', " +
                    "       tr.amount, " +
                    "       tr.currency, " +
                    "       NULL, " +
                    "       NULL, " +
                    "       NULL, " +
                    "       NULL, " +
                    "       (SELECT u.username FROM virtual_wallet.user u WHERE u.id = w.owner_id), " +
                    "       tr.status, " +
                    "       tr.date " +
                    "   FROM virtual_wallet.transfer tr " +
                    "   JOIN virtual_wallet.wallet w ON tr.wallet_id = w.id " +
                    "   WHERE tr.wallet_id = :walletId " +

                    "   UNION " +

                    "   SELECT " +
                    "       e.id, " +
                    "       'EXCHANGE', " +
                    "       e.amount, " +
                    "       NULL, " +
                    "       e.from_currency, " +
                    "       e.to_currency, " +
                    "       e.exchange_rate, " +
                    "       (SELECT u.username FROM virtual_wallet.user u WHERE u.id = fw.owner_id), " +
                    "       NULL, " +
                    "       NULL, " +
                    "       e.date " +
                    "   FROM virtual_wallet.exchange e " +
                    "   JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
                    "   JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
                    "   WHERE fw.id = :walletId OR tw.id = :walletId " +
                    ") AS activity " +

                    "WHERE " +
                    "   (:startDate IS NULL OR activity.date BETWEEN :startDate AND :endDate) " +
                    "   AND (:transactionType IS NULL OR activity.type = :transactionType) " +

                    "ORDER BY " +
                    "   CASE " +
                    "       WHEN ?#{#pageable.sort.getOrders().get(0).getProperty()} = 'date' THEN activity.date " +
                    "       WHEN ?#{#pageable.sort.getOrders().get(0).getProperty()} = 'type' THEN activity.type " +
                    "   END " +
                    "   ?#{#pageable.sort.getOrders().get(0).getDirection()} " +

                    "LIMIT ?#{#pageable.pageSize} " +
                    "OFFSET ?#{#pageable.offset}",
            countQuery =
                    "SELECT COUNT(*) FROM (" +
                            "   SELECT " +
                            "       t.id, " +
                            "       'TRANSACTION' AS type, " +
                            "       t.date AS date " +
                            "   FROM virtual_wallet.transaction t " +
                            "   JOIN virtual_wallet.wallet sw ON t.sender_wallet_id = sw.id " +
                            "   JOIN virtual_wallet.wallet rw ON t.recipient_wallet_id = rw.id " +
                            "   WHERE sw.id = :walletId OR rw.id = :walletId " +

                            "   UNION " +

                            "   SELECT " +
                            "       tr.id, " +
                            "       'WITHDRAW' AS type, " +
                            "       tr.date AS date " +
                            "   FROM virtual_wallet.transfer tr " +
                            "   JOIN virtual_wallet.wallet w ON tr.wallet_id = w.id " +
                            "   WHERE tr.wallet_id = :walletId " +

                            "   UNION " +

                            "   SELECT " +
                            "       e.id, " +
                            "       'EXCHANGE' AS type, " +
                            "       e.date AS date " +
                            "   FROM virtual_wallet.exchange e " +
                            "   JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
                            "   JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
                            "   WHERE fw.id = :walletId OR tw.id = :walletId " +
                            ") AS activity " +

                            "WHERE " +
                            "   (:startDate IS NULL OR activity.date BETWEEN :startDate AND :endDate) " +
                            "   AND (:transactionType IS NULL OR activity.type = :transactionType)",
            nativeQuery = true)
    Page<ActivityOutput> findActivitiesByWalletId(
            @Param("walletId") UUID walletId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("transactionType") String transactionType,
            Pageable pageable);*/

}
