package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import jakarta.persistence.*;
import lombok.*;
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
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column
    private String photo = "default photo";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @SQLRestriction("is_deleted = false")
    private Set<Wallet> wallets;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @SQLRestriction("is_deleted = false")
    private Set<Card> cards;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.role = Role.USER;
        this.status = AccountStatus.PENDING;
        this.cards = new HashSet<>();
        this.wallets = new HashSet<>();
    }

    @PreRemove
    protected void onDelete() {
        this.markAsDeleted();
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
        return !status.equals(AccountStatus.PENDING);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !status.equals(AccountStatus.DELETED);
    }

    @Override
    public boolean isEnabled() {
        return !status.equals(AccountStatus.BLOCKED);
    }
}