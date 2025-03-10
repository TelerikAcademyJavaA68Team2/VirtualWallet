package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransferSpecification {
    public static Specification<Transfer> buildTransferSpecification(TransferFilterOptions filterOptions) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1) If userId is present, limit to that user's transfers
            filterOptions.getUserId().ifPresent(userId -> {
                // The user must own either the wallet OR the card
                Predicate walletOwnerIsUser = cb.equal(root.get("wallet").get("owner").get("id"), userId);
                Predicate cardOwnerIsUser = cb.equal(root.get("card").get("owner").get("id"), userId);
                predicates.add(cb.or(walletOwnerIsUser, cardOwnerIsUser));
            });

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
                    // Ignore or throw custom error
                }
            });

            // 4) Filter by status
            filterOptions.getStatus().ifPresent(statusStr -> {
                try {
                    TransactionStatus statusEnum = TransactionStatus.valueOf(statusStr.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // Ignore or throw custom error
                }
            });

            // 5) Filter by card number (e.g. if user typed "1234123412341234")
            filterOptions.getCardNumber().ifPresent(number -> {
                // cardNumber is presumably a String in your Card entity
                // We'll do an equality match. If  partial match, - cb.like(...), cb.equal for full match
                predicates.add(cb.like(root.get("card").get("cardNumber"), number));
            });

            // Combine all predicates
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
