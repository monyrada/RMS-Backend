package rmsbackend.dto.menus.ingredient;

import lombok.Data;
import rmsbackend.enums.StockStatus;

@Data
public class IngredientRequest {

    private String name;
    private String nameKh;
    private String unit;
    private String description;
    private StockStatus stockStatus;

}
