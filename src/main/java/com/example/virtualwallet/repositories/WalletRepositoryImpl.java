package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletRepositoryImpl implements WalletRepository {

    private final SessionFactory sessionFactory;


    @Override
    public void create(Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(wallet);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(wallet);
            session.getTransaction().commit();
        }
    }

    @Override
    public Wallet findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> users = session.createQuery("From Wallet Where id = :id", Wallet.class);
            users.setParameter("id", id);
            return users
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Wallet", id));
        }
    }

    @Override
    public Optional<Wallet> findByUsernameAndCurrency(String userUsername, String currency) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery("From Wallet Where owner.username = :username and currency = :currency", Wallet.class);
            query.setParameter("username", userUsername);
            query.setParameter("currency", currency);
            return query.stream().findFirst();
        }
    }

    @Override
    public List<Wallet> getActiveWalletsByUserId(UUID userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery("From Wallet Where owner.id = :userId and isDeleted = false ", Wallet.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }

    @Override
    public WalletPageOutput getWalletHistory(UUID walletId, int page, int size) {
        return null;
    }
}