package rmsbackend.dto.order;

import lombok.Data;
import rmsbackend.enums.orders.OrderSource;
import rmsbackend.enums.orders.OrderStatus;

@Data
public class OrderFilterRequest {

    private String tableId;
    private OrderStatus status;
    private OrderSource source;

}
