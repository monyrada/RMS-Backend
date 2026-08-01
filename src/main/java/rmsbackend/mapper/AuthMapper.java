package rmsbackend.mapper;

import org.springframework.stereotype.Component;
import rmsbackend.domain.auth.RefreshToken;
import rmsbackend.domain.users.User;
import rmsbackend.dto.auth.response.SessionResponse;
import rmsbackend.dto.auth.response.UserSummaryResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class AuthMapper {

    public UserSummaryResponse toUserSummary(User user) {
        String fullName = Stream.of(user.getFirstname(), user.getLastname())
                .filter(value -> !value.isBlank())
                .reduce((first, second) -> first + " " + second)
                .orElse(null);

        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(fullName)
                .roles(Set.of())
                .build();
    }

    public SessionResponse toSessionResponse(RefreshToken refreshToken) {
        return new SessionResponse(
                refreshToken.getId().toString(),
                refreshToken.getDeviceInfo(),
                refreshToken.getIpAddress(),
                refreshToken.getCreatedAt(),
                refreshToken.getExpiryDate(),
                refreshToken.isRevoked(),
                refreshToken.isActive()
        );
    }

    public List<SessionResponse> toSessionResponseList(List<RefreshToken> refreshTokens) {
        return refreshTokens.stream()
                .map(this::toSessionResponse)
                .toList();
    }
}
