package rmsbackend.mapper;

import org.springframework.stereotype.Component;
import rmsbackend.domain.permissions.Permission;
import rmsbackend.domain.roles.Role;
import rmsbackend.domain.users.User;
import rmsbackend.dto.users.UserRequest;
import rmsbackend.dto.users.UserResponse;

import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    /**
     * Builds a new entity from a create request. Password is intentionally
     * left to the caller to set separately, after encoding it — mappers
     * should never handle raw credentials.
     */
    public User toEntity(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .profileImage(request.getProfileImage())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .build();
    }

    /**
     * Mutates an existing managed entity in place;
     * caller persists via dirty checking or save().
     * */
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .profileImage(user.getProfileImage())
                .dateOfBirth(user.getDateOfBirth())
                .status(user.getStatus())
                .enabled(user.getEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toCollection(TreeSet::new)))
                .permissions(user.getRoles().stream()
                        .filter(role -> Boolean.TRUE.equals(role.getEnabled()))
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getCode)
                        .collect(Collectors.toCollection(TreeSet::new)))
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
