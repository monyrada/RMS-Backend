package rmsbackend.dto.table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import rmsbackend.enums.TableStatus;

@Getter
@Setter
public class RestaurantTableRequest {

    @NotBlank(message = "Table number is required.")
    private String tableNumber;

    @NotNull(message = "Capacity is required.")
    @Min(value = 1, message = "Capacity must be at least 1.")
    private Integer capacity;

    @NotBlank(message = "Location is required.")
    private String location;

    private TableStatus status;
    private Boolean isActive;
}
