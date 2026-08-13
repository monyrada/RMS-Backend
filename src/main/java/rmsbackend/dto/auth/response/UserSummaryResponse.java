package rmsbackend.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class UserSummaryResponse {

    private String id;
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;

}
