package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.enums.Currency;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> buildTransactionSpecification(TransactionFilterOptions filterOptions) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1) If userId is present, add a predicate restricting to that user’s transactions
            filterOptions.getUserId().ifPresent(userId -> {
                // The transaction is relevant if the user is the sender OR the recipient
                Predicate senderOrRecipientIsUser = cb.or(
                        cb.equal(root.get("senderWallet").get("owner").get("id"), userId),
                        cb.equal(root.get("recipientWallet").get("owner").get("id"), userId)
                );
                predicates.add(senderOrRecipientIsUser);
            });
            // If userId is empty, we don't add any user-based restriction => "all transactions"

            // 2) Filter by date range
            filterOptions.getMinCreatedAt().ifPresent(minDate ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("date"), minDate))
            );
            filterOptions.getMaxCreatedAt().ifPresent(maxDate ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("date"), maxDate))
            );

            // 3) Filter by currency
            filterOptions.getCurrency().ifPresent(currencyStr -> {
                try {
                    Currency currencyEnum = Currency.valueOf(currencyStr.toUpperCase());
                    predicates.add(cb.equal(root.get("currency"), currencyEnum));
                } catch (IllegalArgumentException e) {
                    // If invalid, ignore or throw
                }
            });

            // 4) Filter by direction only if userId is present (direction doesn’t really make sense if no userId).
            //    But if you want direction to apply even for "all transactions," just remove this check.
            if (filterOptions.getUserId().isPresent()) {
                filterOptions.getDirection().ifPresent(direction -> {
                    UUID userId = filterOptions.getUserId().get();
                    switch (direction.toLowerCase()) {
                        case "incoming" -> predicates.add(
                                cb.equal(root.get("recipientWallet").get("owner").get("id"), userId)
                        );
                        case "outgoing" -> predicates.add(
                                cb.equal(root.get("senderWallet").get("owner").get("id"), userId)
                        );
                    }
                });
            }

            // 5) Filter by sender username
            filterOptions.getSender().ifPresent(senderStr ->
                    predicates.add(cb.equal(root.get("senderWallet").get("owner").get("username"), senderStr))
            );

            // 6) Filter by recipient username
            filterOptions.getRecipient().ifPresent(recipientStr ->
                    predicates.add(cb.equal(root.get("recipientWallet").get("owner").get("username"), recipientStr))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

