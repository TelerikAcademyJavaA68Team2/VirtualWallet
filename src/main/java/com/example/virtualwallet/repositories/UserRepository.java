package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.user.UserOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    @Query("""
    SELECT NEW com.example.virtualwallet.models.dtos.user.UserOutput(
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
    List<UserOutput> findAllUsersWithTotalBalance();


    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
}