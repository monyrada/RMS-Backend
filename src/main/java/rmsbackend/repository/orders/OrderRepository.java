package rmsbackend.repository.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rmsbackend.domain.orders.Order;
import rmsbackend.enums.orders.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByTableIdAndStatusIn(String tableId, List<OrderStatus> statuses);
    boolean existsByTableIdAndStatusIn(String tableId, List<OrderStatus> statuses);

}
