package rmsbackend.dto.permissions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {

    private String id;
    private String code;
    private String description;

}
