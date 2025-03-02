package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

 /*   @Query("""
            SELECT NEW com.example.virtualwallet.models.Dtos.UserOutput(
                u.id,
                u.username,
                u.email,
                u.phoneNumber,
                u.role,
                u.status,
                CAST(COALESCE(SUM(w.balance), 0) AS BIGDECIMAL)
            )
            FROM User u
            LEFT JOIN Wallet w ON u.id = w.owner.id
            WHERE (:username IS NULL OR u.username = :username)
              AND (:email IS NULL OR u.email = :email)
              AND (:phoneNumber IS NULL OR u.phoneNumber LIKE CONCAT('%', :phoneNumber, '%'))
              AND (:role IS NULL OR u.role = :role)
              AND (:accountStatus IS NULL OR (u.status = :acountStatus))
            GROUP BY u.id, u.username, u.email, u.phoneNumber, u.role, u.status
            HAVING (:minTotalBalance IS NULL OR COALESCE(SUM(w.balance), 0) >= :minTotalBalance)
               AND (:maxTotalBalance IS NULL OR COALESCE(SUM(w.balance), 0) <= :maxTotalBalance)
            """)
    Page<UserOutput> findUsersWithTotalBalance(
            @Param("username") String username,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("role") Role role,
            @Param("accountStatus") String accountStatus,
            @Param("minTotalBalance") BigDecimal minTotalBalance,
            @Param("maxTotalBalance") BigDecimal maxTotalBalance,
            Pageable pageable);
*/
/*
    @Query("""
    SELECT NEW com.example.virtualwallet.models.Dtos.UserOutput(
        u.id,
        u.username,
        u.email,
        u.phoneNumber,
        u.role,
        u.status,
        CAST(COALESCE(SUM(w.balance), 0) AS BIGDECIMAL)
    )
    FROM User u
    LEFT JOIN Wallet w ON u.id = w.owner.id
    GROUP BY u.id, u.username, u.email, u.phoneNumber, u.role, u.status
    """)
    List<UserOutput> findAllUsersWithTotalBalance();*/


    Optional<User> findByUsername(String username);

    User getUserById(UUID id);


}