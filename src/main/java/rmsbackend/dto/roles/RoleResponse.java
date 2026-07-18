package rmsbackend.dto.roles;

import lombok.Builder;
import lombok.Data;
import rmsbackend.domain.roles.Role;

import java.time.LocalDateTime;

@Data
@Builder
public class RoleResponse {

    private String id;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
