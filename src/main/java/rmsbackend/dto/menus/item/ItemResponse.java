package rmsbackend.dto.menus.item;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ItemResponse {

    private String id;
    private String name;
    private BigDecimal price;

}
