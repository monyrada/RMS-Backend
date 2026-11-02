package rmsbackend.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rmsbackend.domain.roles.Role;
import rmsbackend.domain.users.User;
import rmsbackend.enums.UserStatus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public String getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Role> enabledRoles = user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getEnabled()) && role.getName() != null)
                .toList();

        // Roles keep the ROLE_ prefix; permissions are exposed as their bare code (e.g. ORDER_VIEW).
        Stream<GrantedAuthority> roleAuthorities = enabledRoles.stream()
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.getName().trim().toUpperCase().replace(' ', '_')));
        Stream<GrantedAuthority> permissionAuthorities = enabledRoles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getCode()));

        return Stream.concat(roleAuthorities, permissionAuthorities)
                .distinct()
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getId();
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getEnabled()) && user.getStatus() == UserStatus.ACTIVE;
    }
}
