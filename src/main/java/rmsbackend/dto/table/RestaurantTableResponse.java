package rmsbackend.dto.table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import rmsbackend.enums.TableStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RestaurantTableResponse {

    private String id;
    private String tableNumber;
    private Integer capacity;
    private String location;
    private TableStatus status;

    /** Deep link encoded inside the table's QR code, pointing customers to the ordering page. */
    private String qrCodeUrl;

    /** Backend endpoint that streams the printable QR code PNG for this table. */
    private String qrCodeImageUrl;

    /** Name of whoever currently holds the table (active order's guest, or a manually set reservation name). */
    private String currentGuestName;

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
