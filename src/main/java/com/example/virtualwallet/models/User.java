package com.example.virtualwallet.models;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @Column(nullable = false)
    private boolean isBlocked = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column
    private String photo;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @SQLRestriction("is_deleted = false")
    private Set<Wallet> wallets = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @SQLRestriction("is_deleted = false")
    private Set<CreditCard> creditCards = new HashSet<>();

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreRemove
    protected void onDelete() {
        this.markAsDeleted();
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;

        this.wallets.forEach(Wallet::markAsDeleted);
        this.creditCards.forEach(CreditCard::markAsDeleted);
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
        return !isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !isDeleted && !isBlocked;
    }
}