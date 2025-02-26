package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = """
        USE virtual_wallet;
        SELECT u.username, u.email, u.phone_number, u.role, u.is_enabled, COUNT(t.id) AS transaction_count
        FROM user u
        LEFT JOIN wallet w ON u.id = w.wallet_owner_id
        LEFT JOIN transaction t ON w.id = t.sender_wallet_id OR w.id = t.recipient_wallet_id
        WHERE (:username IS NULL OR u.username LIKE CONCAT('%', :username, '%'))
          AND (:email IS NULL OR u.email LIKE CONCAT('%', :email, '%'))
          AND (:phoneNumber IS NULL OR u.phone_number LIKE CONCAT(:phoneNumber, '%'))
          AND (:accountType IS NULL OR u.role = :accountType)
          AND (:accountStatus IS NULL OR (u.is_enabled = CASE WHEN :accountStatus = 'active' THEN TRUE ELSE FALSE END))
        GROUP BY u.username, u.email, u.phone_number, u.role, u.is_enabled
        HAVING COUNT(t.id) >= COALESCE(:minNumberOfTransactions, 0)
           AND COUNT(t.id) <= COALESCE(:maxNumberOfTransactions, 999999)
        ORDER BY CASE WHEN :orderBy = 'username' THEN u.username END ASC,
                 CASE WHEN :orderBy = 'email' THEN u.email END ASC,
                 CASE WHEN :orderBy = 'phone_number' THEN u.phone_number END ASC,
                 CASE WHEN :orderBy = 'role' THEN CASE WHEN u.role = 'ADMIN' THEN 1 ELSE 2 END END ASC,
                 CASE WHEN :orderBy = 'transactions' THEN COUNT(t.id) END DESC
        """, nativeQuery = true)
    Page<Object[]> findFilteredUsers(
            @Param("username") String username,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("accountType") String accountType,
            @Param("accountStatus") String accountStatus,
            @Param("minNumberOfTransactions") Integer minNumberOfTransactions,
            @Param("maxNumberOfTransactions") Integer maxNumberOfTransactions,
            @Param("orderBy") String orderBy,
            Pageable pageable);
}