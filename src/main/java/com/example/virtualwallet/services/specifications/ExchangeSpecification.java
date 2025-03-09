package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.Exchange;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ExchangeSpecification {

    public static Specification<Exchange> buildExchangeSpecification(ExchangeFilterOptions filterOptions) {
        return (Root<Exchange> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getRecipientUsername().ifPresent(recipientUsername ->
                    predicates.add(cb.like(root.get("recipientUsername"), recipientUsername))
            );

            filterOptions.getFromDate().ifPresent(fromDate ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("date"), fromDate))
            );

            filterOptions.getToDate().ifPresent(toDate ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("date"), toDate))
            );

            filterOptions.getFromCurrency().ifPresent(fromCurrency ->
                    predicates.add(cb.equal(root.get("fromCurrency"), fromCurrency))
            );

            filterOptions.getToCurrency().ifPresent(toCurrency ->
                    predicates.add(cb.equal(root.get("toCurrency"), toCurrency))
            );

            filterOptions.getMinStartAmount().ifPresent(amount ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), amount))
            );

            filterOptions.getMaxStartAmount().ifPresent(amount ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("amount"), amount))
            );

            filterOptions.getMinEndAmount().ifPresent(toAmount ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("toAmount"), toAmount))
            );

            filterOptions.getMaxEndAmount().ifPresent(toAmount ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("toAmount"), toAmount))
            );


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}