package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EXAMPLEUserQueryBuilderHelper {


/*

    public UserPageOutput filterUsers(UserFilterOptions userFilterOptions) {
        UserQueryBuilderHelper queryBuilder = new UserQueryBuilderHelper();

        queryBuilder.addUsernameFilter(userFilterOptions.getUsername());
        queryBuilder.addEmailFilter(userFilterOptions.getEmail());
        queryBuilder.addPhoneNumberFilter(userFilterOptions.getPhoneNumber());
        queryBuilder.addRoleFilter(userFilterOptions.getRole());
        queryBuilder.addAccountStatusFilter(userFilterOptions.getAccountStatus());
        queryBuilder.addGroupBy();
        queryBuilder.addMinTotalBalanceFilter(userFilterOptions.getMinTotalBalance());
        queryBuilder.addMaxTotalBalanceFilter(userFilterOptions.getMaxTotalBalance());
        queryBuilder.addSorting(
                userFilterOptions.getSortBy().orElse("u.username"),
                userFilterOptions.getSortOrder().orElse("ASC")
        );

        String queryString = queryBuilder.getQueryString();
        Map<String, Object> parameters = queryBuilder.getParameters();

        TypedQuery<UserOutput> query = entityManager.createQuery(queryString, UserOutput.class);
        parameters.forEach(query::setParameter);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<UserOutput> content = query.getResultList();
        long total = countFilteredUsers(queryBuilder.getCountQuery(), parameters);
        UserPageOutput output = new UserPageOutput();
        output.setContent(content);
        return output;
    }
*/



    @Getter
    private final Map<String, Object> parameters = new HashMap<>();
    private final StringBuilder queryBuilder = new StringBuilder();
    private boolean hasFilters = false;
    private boolean hasAggregateConditions = false;

    public EXAMPLEUserQueryBuilderHelper() {
        queryBuilder.append("SELECT NEW com.example.virtualwallet.models.dtos.user.UserOutput(");
        queryBuilder.append("u.id, u.username, u.email, u.phoneNumber, u.role, u.status, COALESCE(SUM(w.balance), 0)) ");
        queryBuilder.append("FROM User u LEFT JOIN Wallet w ON u.id = w.owner.id ");
    }

    public void addUsernameFilter(Optional<String> username) {
        appendFilter(username, "username", "u.username LIKE :username");
    }

    public void addEmailFilter(Optional<String> email) {
        appendFilter(email, "email", "u.email LIKE :email");
    }

    public void addPhoneNumberFilter(Optional<String> phoneNumber) {
        appendFilter(phoneNumber, "phoneNumber", "u.phoneNumber LIKE :phoneNumber");
    }

    public void addRoleFilter(Optional<Role> role) {
        appendFilter(role, "role", "u.role = :role");
    }

    public void addAccountStatusFilter(Optional<AccountStatus> accountStatus) {
        appendFilter(accountStatus, "accountStatus", "u.status = :accountStatus");
    }

    public void addMinTotalBalanceFilter(Optional<BigDecimal> minTotalBalance) {
        appendAggregateCondition(minTotalBalance, "minTotalBalance", "COALESCE(SUM(w.balance), 0) >= :minTotalBalance");
    }

    public void addMaxTotalBalanceFilter(Optional<BigDecimal> maxTotalBalance) {
        appendAggregateCondition(maxTotalBalance, "maxTotalBalance", "COALESCE(SUM(w.balance), 0) <= :maxTotalBalance");
    }

    public void addGroupBy() {
        queryBuilder.append(" GROUP BY u.id, u.username, u.email, u.phoneNumber, u.role, u.status ");
    }

    public void addSorting(String sortBy, String sortOrder) {

        if (!sortBy.isEmpty() && !sortOrder.isEmpty()) {
            if (sortBy.equals("balance")) {
                sortBy = "COALESCE(SUM(w.balance), 0)";
            }
            queryBuilder.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder);
        }
    }

    public String getQueryString() {
        return queryBuilder.toString();
    }

    public String getCountQuery() {
        if (hasFilters || hasAggregateConditions) {
            StringBuilder filteringPart = new StringBuilder();

            if (hasFilters) {
                int whereIndex = queryBuilder.indexOf("WHERE");
                int groupByIndex = queryBuilder.indexOf("GROUP BY");

                if (whereIndex != -1 && groupByIndex != -1) {
                    filteringPart.append(queryBuilder.substring(whereIndex, groupByIndex));
                }
            }

            filteringPart.append(" GROUP BY u.id ");

            if (hasAggregateConditions) {
                int havingIndex = queryBuilder.indexOf("HAVING");
                int orderIndex = queryBuilder.indexOf("ORDER BY");

                if (havingIndex != -1) {
                    if (orderIndex != -1) {
                        filteringPart.append(queryBuilder.substring(havingIndex, orderIndex));
                    } else {
                        filteringPart.append(queryBuilder.substring(havingIndex));
                    }
                }
            }
            return "SELECT COUNT(DISTINCT u.id) FROM User u LEFT JOIN Wallet w ON u.id = w.owner.id " +
                    filteringPart.toString();
        } else {
            return "SELECT COUNT(DISTINCT u.id) FROM User u LEFT JOIN Wallet w ON u.id = w.owner.id GROUP BY u.id";
        }
    }

    private void appendFilter(Optional<?> value, String parameterName, String condition) {
        value.ifPresent(v -> {
            if (!hasFilters) {
                queryBuilder.append(" WHERE ");
            } else {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append(condition).append(" ");
            parameters.put(parameterName, v instanceof String ? "%" + v + "%" : v);
            hasFilters = true;
        });
    }

    private void appendAggregateCondition(Optional<BigDecimal> value, String parameterName, String condition) {
        value.ifPresent(v -> {
            if (!hasAggregateConditions) {
                queryBuilder.append(" HAVING ");
            } else {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append(condition).append(" ");
            parameters.put(parameterName, v);
            hasAggregateConditions = true;
        });
    }
}