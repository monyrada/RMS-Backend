package rmsbackend.dto.users;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rmsbackend.enums.Gender;
import rmsbackend.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UserRequest {

    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private Gender gender;
    private LocalDate dateOfBirth;
    private UserStatus status;
    private Boolean enabled;
}
