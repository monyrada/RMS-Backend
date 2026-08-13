package rmsbackend.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(example = "9f8c6e2a-3b1d-4e5f-8a2b-1c2d3e4f5a6b")
    private String refreshToken;

    @Schema(example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Access token lifetime in seconds", example = "900")
    private long expiresIn;

    private UserSummaryResponse user;

}
