package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.ActivityOutput;
import com.example.virtualwallet.models.enums.Currency;
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

            "SELECT e.id, 'EXCHANGE', e.amount, NULL, e.from_currency, e.to_currency, e.exchange_rate, " +
            "(SELECT u.username FROM virtual_wallet.user u WHERE u.id = fw.owner_id), NULL, NULL, e.date " +
            "FROM virtual_wallet.exchange e " +
            "JOIN virtual_wallet.wallet fw ON e.from_wallet_id = fw.id " +
            "JOIN virtual_wallet.wallet tw ON e.to_wallet_id = tw.id " +
            "WHERE fw.id = :walletId OR tw.id = :walletId " +
            ") AS activity " +
            "ORDER BY activity.date DESC", nativeQuery = true)
    List<ActivityOutput> findActivitiesByWalletId(@Param("walletId") UUID walletId);

}
