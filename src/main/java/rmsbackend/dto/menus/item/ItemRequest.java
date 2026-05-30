package rmsbackend.dto.menus.item;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {

    private String categoryId;
    private String name;
    private String nameKh;
    private BigDecimal price;
    private String description;

}
