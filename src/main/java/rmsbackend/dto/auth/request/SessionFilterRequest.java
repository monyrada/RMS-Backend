package rmsbackend.dto.auth.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionFilterRequest {

    private Boolean revoked;
    private String deviceInfoContains;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;

}
