package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(unique = true, nullable = false, updatable = false, name = "username")
    private String username;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(unique = true, nullable = false, name = "email")
    private String email;

    @Column(unique = true, nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(name = "photo")
    private String photo = "default photo";

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false, name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @SQLRestriction("is_deleted = false")
    private Set<Wallet> wallets;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @SQLRestriction("is_deleted = false")
    private Set<Card> cards;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public User(String firstName,
                String lastName,
                String username,
                String password,
                String email,
                String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.photo = "defaultPhoto";
        this.phoneNumber = phoneNumber;
        this.createdAt = LocalDateTime.now();
        this.role = Role.USER;
        this.status = AccountStatus.ACTIVE; // change to pending if email verification is active
        this.cards = new HashSet<>();
        this.wallets = new HashSet<>();
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
        this.status = AccountStatus.DELETED;

        this.wallets.forEach(Wallet::markAsDeleted);
        this.cards.forEach(Card::markAsDeleted);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(AccountStatus.PENDING);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status.equals(AccountStatus.DELETED) || status.equals(AccountStatus.BLOCKED_AND_DELETED);
    }

    @Override
    public boolean isEnabled() {
        return status.equals(AccountStatus.BLOCKED) || status.equals(AccountStatus.BLOCKED_AND_DELETED);
    }
}