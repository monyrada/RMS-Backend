package rmsbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rmsbackend.enums.orders.OrderSource;
import rmsbackend.enums.orders.OrderStatus;
import rmsbackend.enums.orders.OrderType;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private String orderNumber;
    //private RestaurantTable table;
    private String tableId;
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

    private List<OrderItemRequest> items;

}
