package rmsbackend.dto.table;

import lombok.Data;
import rmsbackend.enums.TableStatus;

@Data
public class RestaurantTableFilterRequest {

    private String keyword;      // search in tableNumber / location
    private TableStatus status;
    private Boolean isActive;

}
