package com.example.virtualwallet.models.fillterOptions;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransactionStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransferSpecification {

    public static Specification<Transfer> buildtransferSpecification(UUID userId, TransferFilterOptions filterOptions) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1) Ensure the transfer belongs to the given user:
            //    The user must own either the wallet OR the card for this transfer to appear.
            Predicate walletOwnerIsUser = cb.equal(root.get("wallet").get("owner").get("id"), userId);
            Predicate cardOwnerIsUser = cb.equal(root.get("card").get("owner").get("id"), userId);
            predicates.add(cb.or(walletOwnerIsUser, cardOwnerIsUser));

            // 2) Filter by date range (minCreatedAt / maxCreatedAt)
            filterOptions.getMinCreatedAt().ifPresent(minDate ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("date"), minDate))
            );
            filterOptions.getMaxCreatedAt().ifPresent(maxDate ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("date"), maxDate))
            );

            // 3) Filter by currency
            filterOptions.getCurrency().ifPresent(currencyStr -> {
                // If 'currencyStr' must be one of your enums, parse it:
                // e.g. Currency.valueOf("EUR") => Currency.EUR
                try {
                    Currency currencyEnum = Currency.valueOf(currencyStr.toUpperCase());
                    predicates.add(cb.equal(root.get("currency"), currencyEnum));
                } catch (IllegalArgumentException e) {
                    // The user passed an invalid currency string; decide how to handle it
                    // e.g. ignore or rethrow a custom exception
                }
            });

            // 4) Filter by status
            filterOptions.getStatus().ifPresent(statusStr -> {
                try {
                    TransactionStatus statusEnum = TransactionStatus.valueOf(statusStr.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // Invalid status string
                }
            });

            // 5) Filter by card ID
            filterOptions.getCardId().ifPresent(cardIdStr -> {
                try {
                    UUID cardUuid = UUID.fromString(cardIdStr);
                    predicates.add(cb.equal(root.get("card").get("id"), cardUuid));
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format for cardId
                }
            });

            // 6) Filter by wallet ID
            filterOptions.getWalletId().ifPresent(walletIdStr -> {
                try {
                    UUID walletUuid = UUID.fromString(walletIdStr);
                    predicates.add(cb.equal(root.get("wallet").get("id"), walletUuid));
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format for walletId
                }
            });

            // Combine all predicates with AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
