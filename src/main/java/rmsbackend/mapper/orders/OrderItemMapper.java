package rmsbackend.mapper.orders;

import org.springframework.stereotype.Component;
import rmsbackend.domain.menus.Item;
import rmsbackend.domain.orders.Order;
import rmsbackend.domain.orders.OrderItem;
import rmsbackend.dto.order.OrderItemRequest;
import rmsbackend.dto.order.OrderItemResponse;
import rmsbackend.enums.orders.OrderItemStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemMapper {

    public OrderItem toEntity(OrderItemRequest orderItemRequest, Order order, Item menu) {
        BigDecimal unitPrice = menu.getPrice();
        BigDecimal quantity = BigDecimal.valueOf(orderItemRequest.getQuantity());

        return OrderItem.builder()
                .order(order)
                .menu(menu)
                .quantity(orderItemRequest.getQuantity())
                .unitPrice(unitPrice)
                .subtotal(unitPrice.multiply(quantity))
                .status(OrderItemStatus.PENDING)
                .note(orderItemRequest.getNote())
                .build();
    }

    public OrderItemResponse toResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuId(item.getMenu() != null ? item.getMenu().getId() : null)
                .menuName(item.getMenu() != null ? item.getMenu().getName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .status(item.getStatus())
                .note(item.getNote())
                .build();
    }

    public List<OrderItemResponse> toResponseList(List<OrderItem> items) {
        return items.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
