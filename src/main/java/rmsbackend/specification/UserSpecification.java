package rmsbackend.specification;

import org.springframework.data.jpa.domain.Specification;
import rmsbackend.domain.users.User;
import rmsbackend.dto.users.UserFilterRequest;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class UserSpecification {

    public UserSpecification() {}

    public static Specification<User> withFilter(UserFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // search by keyword
            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String likePattern = "%" + filter.getKeyword().toLowerCase() + "%";

                Predicate usernameMatch  = cb.like(cb.lower(root.get("username")), likePattern);
                Predicate firstNameMatch = cb.like(cb.lower(root.get("firstName")), likePattern);
                Predicate lastNameMatch  = cb.like(cb.lower(root.get("lastName")), likePattern);
                Predicate emailMatch     = cb.like(cb.lower(root.get("email")), likePattern);

                predicates.add(cb.or(usernameMatch, firstNameMatch, lastNameMatch, emailMatch));
            }

            // filter by status
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            // filter by gender
            if (filter.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), filter.getGender()));
            }

            // filter by enabled
            if (filter.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), filter.getEnabled()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}