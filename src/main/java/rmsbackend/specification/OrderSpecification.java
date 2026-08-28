package rmsbackend.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import rmsbackend.domain.orders.Order;
import rmsbackend.dto.order.OrderFilterRequest;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> withFilter(OrderFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (filter.getTableId() != null) {
                    predicates.add(cb.equal(root.get("table").get("id"), filter.getTableId()));
                }
                if (filter.getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), filter.getStatus()));
                }
                if (filter.getSource() != null) {
                    predicates.add(cb.equal(root.get("source"), filter.getSource()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}