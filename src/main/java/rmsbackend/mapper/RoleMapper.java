package rmsbackend.mapper;

import org.springframework.stereotype.Component;
import rmsbackend.domain.permissions.Permission;
import rmsbackend.domain.roles.Role;
import rmsbackend.dto.roles.RoleResponse;

import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .enabled(role.getEnabled())
                .permissions(role.getPermissions().stream()
                        .map(Permission::getCode)
                        .collect(Collectors.toCollection(TreeSet::new)))
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

}
