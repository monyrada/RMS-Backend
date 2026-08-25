package rmsbackend.mapper.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rmsbackend.domain.RestaurantTable;
import rmsbackend.domain.orders.Order;
import rmsbackend.dto.order.OrderRequest;
import rmsbackend.dto.order.OrderResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;

    public Order toEntity(OrderRequest orderRequest, RestaurantTable table) {
        return Order.builder()
                .table(table)
                .orderType(orderRequest.getOrderType())
                .source(orderRequest.getSource())
                .status(orderRequest.getStatus())
                .guestCount(orderRequest.getGuestCount())
                .subtotal(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .note(orderRequest.getNote())
                .build();
    }

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .tableId(order.getTable() != null ? order.getTable().getId() : null)
                .tableNumber(order.getTable() != null ? order.getTable().getTableNumber() : null)
                .orderType(order.getOrderType())
                .source(order.getSource())
                .status(order.getStatus())
                .guestCount(order.getGuestCount())
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .tax(order.getTax())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public List<OrderResponse> toResponseList(List<Order> orders) {
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
