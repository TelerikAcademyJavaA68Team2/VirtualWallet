package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransferSpecification {
    public static Specification<Transfer> buildTransferSpecification(TransferFilterOptions filterOptions) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getRecipient().ifPresent(recipient ->
                    predicates.add(cb.like(root.get("recipientUsername"), "%" + recipient + "%"))
            );

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

            filterOptions.getStatus().ifPresent(status ->
                    predicates.add(cb.equal(root.get("status"), status))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}