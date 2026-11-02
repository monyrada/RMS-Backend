package rmsbackend.mapper;

import org.springframework.stereotype.Component;
import rmsbackend.domain.permissions.Permission;
import rmsbackend.dto.permissions.PermissionResponse;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .description(permission.getDescription())
                .build();
    }

}
