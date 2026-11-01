package rmsbackend.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rmsbackend.domain.users.User;
import rmsbackend.enums.UserStatus;

import java.util.Collection;

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
        return user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getEnabled()) && role.getName() != null)
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.getName().trim().toUpperCase().replace(' ', '_')))
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
