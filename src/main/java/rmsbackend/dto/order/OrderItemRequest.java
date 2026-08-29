package rmsbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rmsbackend.enums.orders.OrderItemStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    private String menuId;
    private Integer quantity;
    private OrderItemStatus status;
    private String note;

}
