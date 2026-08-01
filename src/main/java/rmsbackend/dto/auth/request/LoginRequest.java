package rmsbackend.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "User email", example = "admin@rms.local")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "User password", example = "Admin@12345")
    @NotBlank
    private String password;

}
