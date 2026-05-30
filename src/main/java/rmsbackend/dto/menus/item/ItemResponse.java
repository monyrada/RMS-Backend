package rmsbackend.dto.menus.item;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ItemResponse {

    private String id;
    private String categoryId;
    private String name;
    private String nameKh;
    private BigDecimal price;
    private String description;

}
