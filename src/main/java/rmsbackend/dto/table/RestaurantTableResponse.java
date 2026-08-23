package rmsbackend.dto.table;

import lombok.Getter;
import lombok.Setter;
import rmsbackend.enums.TableStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RestaurantTableResponse {

    private String id;
    private String tableNumber;
    private Integer capacity;
    private String location;
    private TableStatus status;
    private String qrCodeUrl;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
