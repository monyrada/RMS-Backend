package rmsbackend.dto.menus.item;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemResponse {

    private String id;
    //private String categoryId;
    private String categoryName;
    private String name;
    private String nameKh;
    private BigDecimal price;
    private String imageUrl;
    private Boolean status;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
