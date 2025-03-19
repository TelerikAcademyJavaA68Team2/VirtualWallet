package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> buildTransactionSpecification(TransactionFilterOptions filterOptions) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getProfileUsername().ifPresent(profileUsername -> {
                Predicate senderOrRecipientIsUser = cb.or(
                        cb.equal(root.get("senderWallet").get("owner").get("username"), profileUsername),
                        cb.equal(root.get("recipientWallet").get("owner").get("username"), profileUsername)
                );
                predicates.add(senderOrRecipientIsUser);
            });

            filterOptions.getFromDate().ifPresent(fromDate ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("date"), fromDate))
            );

            filterOptions.getToDate().ifPresent(toDate ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("date"), toDate))
            );

            filterOptions.getMinAmount().ifPresent(minAmount ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount))
            );

            filterOptions.getMaxAmount().ifPresent(maxAmount ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount))
            );

            filterOptions.getCurrency().ifPresent(currency ->
                    predicates.add(cb.equal(root.get("currency"), currency))
            );

            if (filterOptions.getProfileUsername().isPresent()) {
                filterOptions.getDirection().ifPresent(direction -> {
                    String username = filterOptions.getProfileUsername().get();
                    switch (direction.toLowerCase()) {
                        case "incoming" -> predicates.add(cb.equal(root.get("recipientUsername"), username));
                        case "outgoing" -> predicates.add(cb.equal(root.get("senderUsername"), username));
                    }
                });
            }

            filterOptions.getSender().ifPresent(senderUsername ->
                    predicates.add(cb.like(root.get("senderUsername"), "%" + senderUsername + "%"))
            );

            filterOptions.getRecipient().ifPresent(recipientUsername ->
                    predicates.add(cb.like(root.get("recipientUsername"), "%" + recipientUsername + "%"))
            );

            filterOptions.getDescription().ifPresent(description ->
                    predicates.add(cb.like(root.get("description"), "%" + description + "%"))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

