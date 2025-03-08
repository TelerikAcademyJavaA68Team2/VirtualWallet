package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.models.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> buildTransactionSpecification(UUID userId, TransactionFilterOptions filterOptions) {
        return (Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            // Collect all predicates here
            List<Predicate> predicates = new ArrayList<>();

            // 1) Make sure transaction belongs to the given user (either incoming or outgoing)
            //    ( might tweak logic if "direction" is specified, see below)
            //    By default, to see *all* transactions (incoming + outgoing) of user:
            Predicate senderOrRecipientIsUser = cb.or(
                    cb.equal(root.get("senderWallet").get("owner").get("id"), userId),
                    cb.equal(root.get("recipientWallet").get("owner").get("id"), userId)
            );
            predicates.add(senderOrRecipientIsUser);

            // 2) Filter by date range if specified
            filterOptions.getMinCreatedAt().ifPresent(minDate ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("date"), minDate))
            );
            filterOptions.getMaxCreatedAt().ifPresent(maxDate ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("date"), maxDate))
            );

            // 3) Filter by "direction" (incoming or outgoing)
            //    For example: if "incoming", the transaction's recipient == userId
            //                 if "outgoing", the transaction's sender == userId
            //                 if not provided, keep them all
            filterOptions.getDirection().ifPresent(direction -> {
                switch (direction.toLowerCase()) {
                    case "incoming" -> {
                        predicates.add(
                                cb.equal(root.get("recipientWallet").get("owner").get("id"), userId)
                        );
                    }
                    case "outgoing" -> {
                        predicates.add(
                                cb.equal(root.get("senderWallet").get("owner").get("id"), userId)
                        );
                    }
                    // If "direction" is something else or empty, do nothing
                }
            });

            // 4) Filter by sender username (if present)
            //    This implies a JOIN on "senderWallet.owner.username"
            filterOptions.getSender().ifPresent(senderUsernameOrEmailOrPhone -> {

                predicates.add(
                        cb.equal(root.get("senderWallet").get("owner").get("username"), senderUsernameOrEmailOrPhone)
                );
                // possibly phone/email, expand to a multi-condition
            });

            // 5) Filter by recipient username (if present)
            filterOptions.getRecipient().ifPresent(recipientUsernameOrEmailOrPhone -> {
                predicates.add(
                        cb.equal(root.get("recipientWallet").get("owner").get("username"), recipientUsernameOrEmailOrPhone)
                );
                // phone/email fields if needed
            });

            // Combine everything with AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

