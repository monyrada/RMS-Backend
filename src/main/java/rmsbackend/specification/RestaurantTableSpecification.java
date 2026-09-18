package rmsbackend.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import rmsbackend.domain.RestaurantTable;
import rmsbackend.dto.table.RestaurantTableFilterRequest;

import java.util.ArrayList;
import java.util.List;

public class RestaurantTableSpecification {

    public static Specification<RestaurantTable> withFilter(RestaurantTableFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (filter.getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), filter.getStatus()));
                }
                if (filter.getIsActive() != null) {
                    predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
                }
                if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                    String pattern = "%" + filter.getKeyword().toLowerCase() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("tableNumber")), pattern),
                            cb.like(cb.lower(root.get("location")), pattern)
                    ));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
