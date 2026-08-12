package rmsbackend.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.BusinessException;
import rmsbackend.domain.auth.RefreshToken;
import rmsbackend.domain.users.User;
import rmsbackend.dto.auth.request.SessionFilterRequest;
import rmsbackend.dto.auth.request.LoginRequest;
import rmsbackend.dto.auth.request.RefreshTokenRequest;
import rmsbackend.dto.auth.response.LoginResponse;
import rmsbackend.dto.auth.response.SessionResponse;
import rmsbackend.enums.UserStatus;
import rmsbackend.mapper.AuthMapper;
import rmsbackend.repository.auth.RefreshTokenRepository;
import rmsbackend.repository.users.UserRepository;
import rmsbackend.security.jwt.JwtTokenProvider;
import rmsbackend.specification.auth.RefreshTokenSpecification;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;

    @Value("${app.refresh-token.expiry-minutes:10080}")
    private long refreshTokenExpiryMinutes;

    private final SecureRandom secureRandom = new SecureRandom();

    public LoginResponse login(LoginRequest request, String ipAddress) {
        User user = authenticate(request);

        // Generate JWT token
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        // Generate refresh token
        String refreshToken = createRefreshToken(user, null, ipAddress);

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User [{}] logged in from [{}]", user.getEmail(), ipAddress);

        return buildLoginResponse(user, accessToken, refreshToken);
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        RefreshToken token = validateRefreshToken(request.getRefreshToken());

        revoke(token);

        User user = token.getUser();

        String newRefreshToken = createRefreshToken(
                user,
                token.getDeviceInfo(),
                token.getIpAddress()
        );

        log.info("Refresh token rotated for user [{}]", user.getEmail());

        return buildLoginResponse(
                user,
                jwtTokenProvider.generateAccessToken(user),
                newRefreshToken
        );
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenHash(hash(refreshToken)).ifPresent(this::revoke);

        log.info("Logout completed.");
    }

    public void logoutAllDevices(String userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUserIdAndRevokedFalse(userId);
        tokens.forEach(this::revoke);

        log.info("{} sessions revoked for user [{}]", tokens.size(), userId);
    }

    public List<SessionResponse> listSessions(String userId, SessionFilterRequest filter) {
        return authMapper.toSessionResponseList(
                refreshTokenRepository.findAll(
                        RefreshTokenSpecification.withFilters(userId, filter)
                )
        );
    }

    private User authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BusinessException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password.");
        }

        if (!Boolean.TRUE.equals(user.getEnabled()) || user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User account is disabled or inactive.");
        }

        return user;
    }

    private RefreshToken validateRefreshToken(String rawToken) {

        RefreshToken token = refreshTokenRepository
                .findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BusinessException("Refresh token is invalid."));

        if (!token.isActive()) {
            throw new BusinessException("Refresh token has expired or has been revoked.");
        }

        return token;
    }

    private String createRefreshToken(User user, String deviceInfo, String ipAddress) {
        String rawToken = generateRawToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hash(rawToken))
                .expiryDate(LocalDateTime.now().plusMinutes(refreshTokenExpiryMinutes))
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    private void revoke(RefreshToken token) {
        token.revoke();

        refreshTokenRepository.save(token);
    }

    private LoginResponse buildLoginResponse(User user, String accessToken, String refreshToken) {

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenTtlSeconds())
                .user(authMapper.toUserSummary(user))
                .build();
    }

    private String generateRawToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available.", ex);
        }
    }
}
