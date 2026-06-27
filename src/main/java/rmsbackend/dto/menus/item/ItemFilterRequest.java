package rmsbackend.dto.menus.item;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemFilterRequest {

    private String     categoryId;   // exact match
    private String     keyword;      // search in name / nameKh / description
    private Boolean    status;       // true = active, false = inactive, null = all
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

}
