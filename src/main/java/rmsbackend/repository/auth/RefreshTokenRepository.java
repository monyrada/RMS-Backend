package rmsbackend.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rmsbackend.domain.auth.RefreshToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends
        JpaRepository<RefreshToken, UUID>,
        JpaSpecificationExecutor<RefreshToken> {

    /**
     * Find refresh token by hashed token.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Find all active (not revoked) tokens for a user.
     */
    List<RefreshToken> findByUserIdAndRevokedFalse(String userId);

    /**
     * Delete expired refresh tokens.
     */
    void deleteByExpiryDateBefore(LocalDateTime date);

}