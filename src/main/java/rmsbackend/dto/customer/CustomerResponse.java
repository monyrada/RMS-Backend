package rmsbackend.dto.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CustomerResponse {

    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
