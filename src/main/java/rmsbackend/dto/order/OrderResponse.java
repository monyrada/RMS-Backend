package rmsbackend.dto.order;

import lombok.*;
import rmsbackend.enums.orders.OrderSource;
import rmsbackend.enums.orders.OrderStatus;
import rmsbackend.enums.orders.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private String id;
    private String orderNumber;

    private String tableId;
    private String tableNumber;          // denormalized for display

    private OrderType orderType;
    private OrderSource source;

    private OrderStatus status;
    private Integer guestCount;
    private String guestName;

    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal totalAmount;

    private String note;

    private List<OrderItemResponse> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
