package rmsbackend.repository.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.orders.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    List<OrderItem> findByOrderId(String orderId);

}
