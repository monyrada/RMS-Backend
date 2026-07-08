package rmsbackend.dto.users;

import lombok.*;
import rmsbackend.enums.Gender;
import rmsbackend.enums.UserStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterRequest {

    private String keyword;
    private UserStatus status;
    private Gender gender;
    private Boolean enabled;

}
