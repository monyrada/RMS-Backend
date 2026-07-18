package rmsbackend.mapper;

import org.springframework.stereotype.Component;
import rmsbackend.domain.roles.Role;
import rmsbackend.dto.roles.RoleResponse;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .enabled(role.getEnabled())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

}
