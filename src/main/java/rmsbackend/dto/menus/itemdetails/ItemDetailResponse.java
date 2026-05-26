package rmsbackend.dto.menus.itemdetails;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ItemDetailResponse {

    private String id;
    private String itemName;
    private String ingredientName;
    private BigDecimal quantity;
    private String note;

}
