package rmsbackend.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.users.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByToken(String token);
}
