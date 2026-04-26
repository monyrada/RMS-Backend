package rmsbackend.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "Customer name is required.")
    private String name;

    private String phoneNumber;

    @Email(message = "Invalid email format.")
    private String email;

    private Boolean active;
}
