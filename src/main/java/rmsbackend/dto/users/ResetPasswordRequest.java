package rmsbackend.dto.users;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String token;

    private String newPassword;

}
