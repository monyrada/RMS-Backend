package rmsbackend.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.auth.request.LoginRequest;
import rmsbackend.dto.auth.request.LogoutRequest;
import rmsbackend.dto.auth.request.RefreshTokenRequest;
import rmsbackend.dto.auth.request.SessionFilterRequest;
import rmsbackend.dto.auth.response.LoginResponse;
import rmsbackend.security.UserPrincipal;
import rmsbackend.service.auth.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication APIs", description = "Endpoints for authentication and session management.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login")
    public RespondDTO login(@Valid @RequestBody LoginRequest request, @Parameter(hidden = true) HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request, httpRequest.getRemoteAddr());

        return JSONRespond.respond(response, StatusCode.SUCCESS);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Access Token")
    public RespondDTO refresh(@Valid @RequestBody RefreshTokenRequest request) {

        return JSONRespond.respond(authService.refresh(request), StatusCode.SUCCESS);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout")
    public RespondDTO logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());

        return JSONRespond.respond(null, StatusCode.SUCCESS, "Logged out successfully.");
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout all devices", security = @SecurityRequirement(name = "bearerAuth"))
    public RespondDTO logoutAllDevices(@Parameter(hidden = true) Authentication authentication) {
        authService.logoutAllDevices(currentUserId(authentication));

        return JSONRespond.respond(null, StatusCode.SUCCESS, "All sessions logged out successfully.");
    }

    @GetMapping("/sessions")
    @Operation(summary = "List current user sessions", security = @SecurityRequirement(name = "bearerAuth"))
    public RespondDTO listSessions(
            @ModelAttribute SessionFilterRequest request,
            @Parameter(hidden = true) Authentication authentication) {

        return JSONRespond.respond(
                authService.listSessions(currentUserId(authentication), request),
                StatusCode.SUCCESS
        );
    }

    private String currentUserId(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return principal.getId();
    }
}
