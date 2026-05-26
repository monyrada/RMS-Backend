package rmsbackend.dto.menus.category;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CategoryRequest {

    private String name;
    private String code;
    private Boolean status;
    private String description;

}
