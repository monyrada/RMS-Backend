package rmsbackend.dto.roles;

import lombok.Data;

@Data
public class RoleRequest {

    private String name;
    private String description;
    private Boolean enabled;

}
