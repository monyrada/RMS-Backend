package rmsbackend.dto.menus.itemdetails;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemDetailResponse {

    private String id;
    private String itemId;
    private String ingredientId;
    private BigDecimal quantity;
    private String note;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
