package rmsbackend.dto.menus.ingredient;

import lombok.Builder;
import lombok.Data;
import rmsbackend.enums.StockStatus;

@Data
@Builder
public class IngredientResponse {

    private String id;
    private String name;
    private String unit;
    private StockStatus stockStatus;

}
