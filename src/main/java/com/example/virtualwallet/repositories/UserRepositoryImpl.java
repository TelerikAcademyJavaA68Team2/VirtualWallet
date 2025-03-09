package com.example.virtualwallet.repositories;

import com.example.virtualwallet.exceptions.EmptyPageException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.dtos.user.UserOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;


    @Override
    public void createUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> users = session.createQuery("From User Where username = :username", User.class);
            users.setParameter("username", username);
            return users
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid username!"));
        }
    }

    @Override
    public User getById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> users = session.createQuery("From User Where id = :id", User.class);
            users.setParameter("id", id);
            return users
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("User", id));
        }
    }

    @Override
    public String findByUsernameOrEmailOrPhoneNumber(String input) {
        try (Session session = sessionFactory.openSession()) {
            String query =
                    "SELECT u.username " +
                            "From User u " +
                            "Where (" +
                            "u.username = :username " +
                            "or u.email = :email " +
                            "or u.phoneNumber = :phoneNumber)";

            Query<String> users = session.createQuery(query, String.class);
            users.setParameter("username", input);
            users.setParameter("email", input);
            users.setParameter("phoneNumber", input);
            return users
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("User"));
        }
    }

    @Override
    public UserPageOutput filterUsers(UserFilterOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {

            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sb = new StringBuilder("FROM User u ");

            if (
                    filterOptions.getUsername().isPresent() ||
                            filterOptions.getEmail().isPresent() ||
                            filterOptions.getRole().isPresent() ||
                            filterOptions.getStatus().isPresent() ||
                            filterOptions.getPhoneNumber().isPresent()
            ) {

                sb.append("WHERE ");

                filterOptions.getUsername().ifPresent(username -> {
                    sb.append("u.username like :username ");
                    parameters.put("username", username);
                    sb.append("AND ");
                });

                filterOptions.getEmail().ifPresent(email -> {
                    sb.append("u.email like :email ");
                    parameters.put("email", email);
                    sb.append("AND ");
                });

                filterOptions.getPhoneNumber().ifPresent(phoneNumber -> {
                    sb.append("u.phoneNumber like :phoneNumber ");
                    parameters.put("phoneNumber", phoneNumber);
                    sb.append("AND ");
                });

                filterOptions.getStatus().ifPresent(status -> {
                    sb.append("u.status = :status ");
                    parameters.put("status", status);
                    sb.append("AND ");
                });

                filterOptions.getRole().ifPresent(role -> {
                    sb.append("u.role = :role ");
                    parameters.put("role", role);
                    sb.append("AND ");
                });

                filterOptions.getMinCreatedAt().ifPresent(minCreatedAt -> {
                    sb.append("u.createdAt >= :minCreatedAt ");
                    parameters.put("minCreatedAt", minCreatedAt);
                    sb.append("AND ");
                });

                filterOptions.getMaxCreatedAt().ifPresent(maxCreatedAt -> {
                    sb.append("u.createdAt <= :maxCreatedAt ");
                    parameters.put("maxCreatedAt", maxCreatedAt);
                    sb.append("AND ");
                });

                sb.setLength(sb.length() - 4);
            }

            // counting
            String countQueryString = "SELECT COUNT(u) " + sb.toString();
            Query<Long> countQuery = session.createQuery(countQueryString, Long.class);
            parameters.forEach(countQuery::setParameter);

            Long numberOfFoundElements = countQuery.getSingleResult();
            int pageSize = filterOptions.getSize();
            int pageStartIndex = filterOptions.getPage() * pageSize;
            if (numberOfFoundElements == null || pageStartIndex > numberOfFoundElements) {
                throw new EmptyPageException();
            }


            String sortBy = filterOptions.getSortBy();
            String sortOrder = filterOptions.getSortOrder();
            sb.append("ORDER BY ").append(sortBy).append(" ").append(sortOrder);

            Query<User> query = session.createQuery(sb.toString(), User.class);
            parameters.forEach(countQuery::setParameter);
            query.setFirstResult(pageStartIndex);
            query.setMaxResults(pageSize);

            List<User> usersList = query.list();
            List<UserOutput> usersOutput = usersList.stream().map(ModelMapper::userOutputFromUser).toList();

            UserPageOutput output = new UserPageOutput();
            output.setContent(usersOutput);
            output.setTotalResults(Math.toIntExact(numberOfFoundElements));
            output.setNumberOfPages((int) (numberOfFoundElements / pageSize));

            return output;
        }


    }

    @Override
    public boolean checkIfEmailIsTaken(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> users = session.createQuery("From User Where email = :email", User.class);
            users.setParameter("email", email);
            return users.uniqueResult() != null;
        }
    }

    @Override
    public boolean checkIfPhoneNumberIsTaken(String phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> users = session.createQuery("From User Where phoneNumber = :phoneNumber", User.class);
            users.setParameter("phoneNumber", phoneNumber);
            return users.uniqueResult() != null;
        }
    }

    @Override
    public boolean checkIfUsernameIsTaken(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> users = session.createQuery("From User Where username = :username", User.class);
            users.setParameter("username", username);
            return users.uniqueResult() != null;
        }
    }
}
