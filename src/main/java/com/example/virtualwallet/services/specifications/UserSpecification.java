package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> buildUserSpecification(UserFilterOptions filterOptions) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getUsername().ifPresent(username ->
                    predicates.add(cb.like(root.get("username"), "%" + username + "%"))
            );

            filterOptions.getEmail().ifPresent(email ->
                    predicates.add(cb.like(root.get("email"), "%" + email + "%"))
            );

            filterOptions.getPhoneNumber().ifPresent(phoneNumber ->
                    predicates.add(cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%"))
            );

            filterOptions.getRole().ifPresent(role ->
                    predicates.add(cb.equal(root.get("role"), role))
            );

            if (filterOptions.getStatus().isPresent() && filterOptions.getStatus().get().equals(AccountStatus.BLOCKED)) {
                predicates.add(
                        cb.or(
                                cb.equal(root.get("status"), AccountStatus.BLOCKED),
                                cb.equal(root.get("status"), AccountStatus.BLOCKED_AND_DELETED)
                        )
                );
            }else if (filterOptions.getStatus().isPresent() && filterOptions.getStatus().get().equals(AccountStatus.DELETED)) {
                predicates.add(
                        cb.or(
                                cb.equal(root.get("status"), AccountStatus.DELETED),
                                cb.equal(root.get("status"), AccountStatus.BLOCKED_AND_DELETED)
                        )
                );
            } else {
                filterOptions.getStatus().ifPresent(status ->
                        predicates.add(cb.equal(root.get("status"), status))
                );
            }

            filterOptions.getFromDate().ifPresent(fromDate ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate))
            );

            filterOptions.getToDate().ifPresent(toDate ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}