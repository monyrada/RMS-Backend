package rmsbackend.dto.menus.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {

    private String id;
    private String name;
    private String nameKh;
    private String code;
    private Boolean status;
    private String description;

}
