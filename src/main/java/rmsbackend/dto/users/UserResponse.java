package rmsbackend.dto.users;

import lombok.*;
import rmsbackend.enums.Gender;
import rmsbackend.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private Gender gender;
    private LocalDate dateOfBirth;
    private UserStatus status;
    private Boolean enabled;

    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}