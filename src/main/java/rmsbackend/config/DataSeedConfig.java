package rmsbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import rmsbackend.domain.users.User;
import rmsbackend.enums.UserStatus;
import rmsbackend.repository.users.UserRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeedConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.system-user.enabled:true}")
    private boolean systemUserSeedEnabled;

    @Value("${app.seed.system-user.email:admin@rms.local}")
    private String systemUserEmail;

    @Value("${app.seed.system-user.password:Admin@12345}")
    private String systemUserPassword;

    @Bean
    public CommandLineRunner seedSystemUser() {
        return args -> {
            if (!systemUserSeedEnabled) {
                return;
            }

            if (userRepository.existsByEmail(systemUserEmail)) {
                log.info("System user seed skipped because email [{}] already exists.", systemUserEmail);
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
                    .build();

            userRepository.save(systemUser);
            log.info("System user seeded with email [{}].", systemUserEmail);
        };
    }
}
