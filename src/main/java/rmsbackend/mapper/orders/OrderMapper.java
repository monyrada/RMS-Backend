package rmsbackend.mapper.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rmsbackend.domain.RestaurantTable;
import rmsbackend.domain.orders.Order;
import rmsbackend.domain.orders.OrderItem;
import rmsbackend.dto.order.OrderRequest;
import rmsbackend.dto.order.OrderResponse;
import rmsbackend.enums.orders.OrderStatus;

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
                .status(OrderStatus.PENDING)
                .guestCount(orderRequest.getGuestCount())
                .guestName(orderRequest.getGuestName())
                .subtotal(BigDecimal.ZERO)
                .discount(orderRequest.getDiscount())
                .tax(orderRequest.getTax())
                .totalAmount(BigDecimal.ZERO)
                .note(orderRequest.getNote())
                .build();
    }

    public OrderResponse toResponse(Order order) {
        return toResponse(order, List.of());
    }

    public OrderResponse toResponse(Order order, List<OrderItem> items) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .tableId(order.getTable() != null ? order.getTable().getId() : null)
                .tableNumber(order.getTable() != null ? order.getTable().getTableNumber() : null)
                .orderType(order.getOrderType())
                .source(order.getSource())
                .status(order.getStatus())
                .guestCount(order.getGuestCount())
                .guestName(order.getGuestName())
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .tax(order.getTax())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .items(orderItemMapper.toResponseList(items))
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
