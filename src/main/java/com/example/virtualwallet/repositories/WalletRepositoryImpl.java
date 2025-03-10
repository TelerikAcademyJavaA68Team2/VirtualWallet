package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.pageable.WalletPageOutput;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.dtos.wallet.ActivityOutput;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public Optional<Wallet> findByUsernameAndCurrency(String userUsername, Currency currency) {
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
    public boolean checkIfUserHasActiveWalletWithCurrency(UUID userId, Currency currency) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery("From Wallet Where owner.id = :userId and currency = :currency and isDeleted = false ", Wallet.class);
            query.setParameter("userId", userId);
            query.setParameter("currency", currency);
            return !query.getResultList().isEmpty();
        }
    }

    @Override
    public boolean checkIfUserHasDeletedWalletWithCurrency(UUID userId, Currency currency) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery("From Wallet Where owner.id = :userId and currency = :currency and isDeleted = true ", Wallet.class);
            query.setParameter("userId", userId);
            query.setParameter("currency", currency);
            return !query.getResultList().isEmpty();
        }
    }

    @Override
    public WalletPageOutput getWalletHistory(UUID walletId, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            WalletPageOutput pageOutput = new WalletPageOutput();
            pageOutput.setWalletId(walletId);


            Long totalResults = 0L;
            Query<Long> countTransfers = session.createQuery("select count(id) From Transfer Where wallet.id = :walletId", Long.class);
            countTransfers.setParameter("walletId", walletId);
            totalResults += countTransfers.getSingleResult();

            Query<Long> countTransactions = session.createQuery("select count(id) From Transaction Where senderWallet.id = :walletId or recipientWallet.id = :walletId", Long.class);
            countTransactions.setParameter("walletId", walletId);
            totalResults += countTransactions.getSingleResult();

            Query<Long> countExchanges = session.createQuery("select count(id) From Exchange Where fromWallet.id = :walletId or toWallet.id = :walletId", Long.class);
            countExchanges.setParameter("walletId", walletId);
            totalResults += countExchanges.getSingleResult();


            int pageStartIndex = page * size;

            pageOutput.setHistoryPages((int) (totalResults / size));
            pageOutput.setHistorySize(Math.toIntExact(totalResults));

            if (totalResults == 0 || pageStartIndex > totalResults) {
                pageOutput.setActivities(List.of());
            } else {
                String sql = """
                        SELECT\s
                            t.id AS id,
                            'TRANSACTION' AS activity,
                            t.amount as amount,
                            NULL as toAmount,
                            t.currency AS currency,
                            NULL AS fromCurrency,
                            NULL AS toCurrency,
                            su.username AS senderUsername,
                            ru.username AS recipientUsername,
                            NULL AS status,
                            t.date as date
                        FROM Transaction t
                        JOIN Wallet sw ON t.sender_wallet_id = sw.id
                        JOIN User su ON sw.owner_id = su.id\s
                        JOIN Wallet rw ON t.recipient_wallet_id = rw.id
                        JOIN User ru ON rw.owner_id = ru.id\s
                        WHERE sw.id = :walletId OR rw.id = :walletId
                        
                        UNION ALL
                        
                        SELECT\s
                            tf.id AS id,
                            'ACCOUNT FUNDING' AS activity,
                            tf.amount as amount,
                            NULL as toAmount,
                            tf.currency AS currency,
                            NULL AS fromCurrency,
                            NULL AS toCurrency,
                            NULL AS senderUsername,
                            wu.username AS recipientUsername,
                            tf.status AS status,
                            tf.date as date
                        FROM Transfer tf
                        JOIN wallet w ON tf.wallet_id = w.id
                        JOIN User wu ON w.owner_id = wu.id\s
                        WHERE tf.wallet_id = :walletId
                        
                        UNION ALL
                        
                        SELECT\s
                            e.id AS id,
                            'EXCHANGE' AS activity,
                            e.amount as amount,
                            e.to_amount as toAmount,
                            NULL AS currency,
                            e.from_currency AS fromCurrency,
                            e.to_currency AS toCurrency,
                            NULL AS senderUsername,
                            NULL AS recipientUsername,
                            NULL AS status,
                            e.date as date
                        FROM Exchange e
                        JOIN Wallet fw ON e.from_wallet_id = fw.id
                        JOIN Wallet tw ON e.to_wallet_id = tw.id
                        WHERE fw.id = :walletId OR tw.id = :walletId
                        
                        ORDER BY date DESC""";

                List<ActivityOutput> activities = session.createNativeQuery(sql)
                        .setParameter("walletId", walletId)
                        .setFirstResult(pageStartIndex)
                        .setMaxResults(size)
                        .unwrap(NativeQuery.class)
                        .addScalar("id", UUID.class)
                        .addScalar("activity", String.class)
                        .addScalar("amount", BigDecimal.class)
                        .addScalar("toAmount", BigDecimal.class)
                        .addScalar("currency", String.class)
                        .addScalar("fromCurrency", String.class)
                        .addScalar("toCurrency", String.class)
                        .addScalar("senderUsername", String.class)
                        .addScalar("recipientUsername", String.class)
                        .addScalar("status", String.class)
                        .addScalar("date", LocalDateTime.class)
                        .setResultTransformer(Transformers.aliasToBean(ActivityOutput.class))
                        .getResultList();


                pageOutput.setActivities(activities);
            }

            return pageOutput;
        }
    }
}