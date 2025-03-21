package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @Query("SELECT u.username FROM User u " +
            "WHERE u.username = :checkBox or u.email = :checkBox or u.phoneNumber = :checkBox")
    Optional<String> findUsernameByUsernameOrEmailOrPhoneNumber(@Param("checkBox") String checkBox);

    @Query("SELECT u FROM User u " +
            "WHERE u.username = :checkBox OR u.email = :checkBox OR u.phoneNumber = :checkBox")
    Optional<User> findUserByUsernameOrEmailOrPhoneNumber(@Param("checkBox") String checkBox);

    @Query("SELECT u FROM User u " +
            "WHERE u.username = :username")
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.status != :status")
    Optional<User> getUserById(@Param("id") UUID id, @Param("status") AccountStatus status);


    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM User u WHERE u.phoneNumber = :phoneNumber) THEN true ELSE false END")
    boolean checkIfPhoneNumberIsTaken(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM User u WHERE u.username = :username) THEN true ELSE false END")
    boolean checkIfUsernameIsTaken(@Param("username") String username);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM User u WHERE u.email = :email) THEN true ELSE false END")
    boolean checkIfEmailIsTaken(@Param("email") String email);

}