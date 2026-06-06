package rmsbackend.dto.menus.ingredient;

import lombok.Builder;
import lombok.Data;
import rmsbackend.enums.StockStatus;
import java.time.LocalDateTime;

@Data
@Builder
public class IngredientResponse {

    private String id;
    private String name;
    private String nameKh;
    private String unit;
    private String description;
    private StockStatus stockStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
