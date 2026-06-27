package rmsbackend.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import rmsbackend.domain.menus.Item;
import rmsbackend.dto.menus.item.ItemFilterRequest;
import java.util.ArrayList;
import java.util.List;

public class ItemSpecification {

    public static Specification<Item> withFilter(ItemFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCategoryId() != null && !filter.getCategoryId().isBlank()) {
                predicates.add(cb.equal(root.get("categoryId"), filter.getCategoryId()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String pattern = "%" + filter.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern)
                        //cb.like(cb.lower(root.get("nameKh")),      pattern),
                        //cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}