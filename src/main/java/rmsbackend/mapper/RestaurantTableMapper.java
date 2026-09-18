package rmsbackend.mapper;

import org.springframework.stereotype.Component;
import rmsbackend.domain.RestaurantTable;
import rmsbackend.dto.table.RestaurantTableRequest;
import rmsbackend.dto.table.RestaurantTableResponse;
import rmsbackend.enums.TableStatus;

import java.util.List;

@Component
public class RestaurantTableMapper {

    public RestaurantTable toEntity(RestaurantTableRequest request) {
        return RestaurantTable.builder()
                .tableNumber(request.getTableNumber().trim())
                .capacity(request.getCapacity())
                .location(request.getLocation().trim())
                .status(request.getStatus() != null ? request.getStatus() : TableStatus.AVAILABLE)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .currentGuestName(normalizeGuestName(request.getCurrentGuestName()))
                .build();
    }

    public void updateEntity(RestaurantTable table, RestaurantTableRequest request) {
        table.setTableNumber(request.getTableNumber().trim());
        table.setCapacity(request.getCapacity());
        table.setLocation(request.getLocation().trim());

        if (request.getStatus() != null) {
            table.setStatus(request.getStatus());
        }
        if (request.getIsActive() != null) {
            table.setIsActive(request.getIsActive());
        }

        if (request.getCurrentGuestName() != null) {
            table.setCurrentGuestName(normalizeGuestName(request.getCurrentGuestName()));
        } else if (request.getStatus() == TableStatus.AVAILABLE || request.getStatus() == TableStatus.CLEANING) {
            // Clearing the table (marking it available/being cleaned) drops any stale guest name.
            table.setCurrentGuestName(null);
        }
    }

    public RestaurantTableResponse toResponse(RestaurantTable table) {
        return RestaurantTableResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .location(table.getLocation())
                .status(table.getStatus())
                .qrCodeUrl(table.getQrCodeUrl())
                .qrCodeImageUrl("/api/v1/tables/" + table.getId() + "/qr-code")
                .currentGuestName(table.getCurrentGuestName())
                .isActive(table.getIsActive())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .build();
    }

    private String normalizeGuestName(String guestName) {
        if (guestName == null || guestName.isBlank()) {
            return null;
        }
        return guestName.trim();
    }

    public List<RestaurantTableResponse> toResponseList(List<RestaurantTable> tables) {
        return tables.stream().map(this::toResponse).toList();
    }
}
