package rmsbackend.dto.menus.itemdetails;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDetailRequest {

    private String itemId;
    private String ingredientId;
    private BigDecimal quantity;
    private String note;

}
