package rmsbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.annotation.Order;
import rmsbackend.domain.roles.Role;
import rmsbackend.domain.users.User;
import rmsbackend.enums.RoleName;
import rmsbackend.enums.UserStatus;
import rmsbackend.repository.roles.RoleRepository;
import rmsbackend.repository.users.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeedConfig {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.system-user.enabled:true}")
    private boolean systemUserSeedEnabled;

    @Value("${app.seed.system-user.email:admin@rms.local}")
    private String systemUserEmail;

    @Value("${app.seed.system-user.password:Admin@12345}")
    private String systemUserPassword;

    @Bean
    @Order(1)
    public CommandLineRunner seedRoles() {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                if (!roleRepository.existsByNameIgnoreCase(roleName.name())) {
                    roleRepository.save(Role.builder()
                            .name(roleName.name())
                            .description(roleName.getDescription())
                            .enabled(true)
                            .build());
                    log.info("Role [{}] seeded.", roleName);
                }
            }
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner seedSystemUser() {
        return args -> {
            if (!systemUserSeedEnabled) {
                return;
            }

            Role adminRole = roleRepository.findByNameIgnoreCase(RoleName.ADMIN.name())
                    .orElseThrow(() -> new IllegalStateException("ADMIN role was not seeded."));

            var existing = userRepository.findByEmail(systemUserEmail);
            if (existing.isPresent()) {
                // Users created before RBAC have no roles; make sure the seeded admin is not locked out.
                User user = existing.get();
                if (user.getRoles().stream().noneMatch(r -> r.getId().equals(adminRole.getId()))) {
                    user.getRoles().add(adminRole);
                    userRepository.save(user);
                    log.info("ADMIN role granted to existing system user [{}].", systemUserEmail);
                }
                return;
            }

            User systemUser = User.builder()
                    .username("system-admin")
                    .firstname("System")
                    .lastname("Administrator")
                    .email(systemUserEmail)
                    .password(passwordEncoder.encode(systemUserPassword))
                    .enabled(true)
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();

            userRepository.save(systemUser);
            log.info("System user seeded with email [{}].", systemUserEmail);
        };
    }
}
