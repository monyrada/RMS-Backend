package rmsbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rmsbackend.domain.RestaurantTable;
import rmsbackend.dto.table.RestaurantTableRequest;
import rmsbackend.dto.table.RestaurantTableResponse;
import rmsbackend.enums.TableStatus;
import rmsbackend.repository.RestaurantTableRepository;
import rmsbackend.service.RestaurantTableService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    @Override
    public RestaurantTableResponse create(RestaurantTableRequest request) {
        String tableNumber = request.getTableNumber().trim();
        if (tableRepository.existsByTableNumber(tableNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Table number already exists.");
        }

        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(tableNumber);
        table.setCapacity(request.getCapacity());
        table.setLocation(request.getLocation().trim());
        table.setStatus(request.getStatus() != null ? request.getStatus() : TableStatus.AVAILABLE);
        table.setIsActive(request.getIsActive() != null ? request.getIsActive(): true );
        table.setQrCodeUrl("/menu?tableNumber=" + tableNumber);

        return mapToResponse(tableRepository.save(table));
    }

    private RestaurantTableResponse mapToResponse(RestaurantTable table) {
        RestaurantTableResponse response = new RestaurantTableResponse();
        response.setId(table.getId());
        response.setTableNumber(table.getTableNumber());
        response.setCapacity(table.getCapacity());
        response.setLocation(table.getLocation());
        response.setStatus(table.getStatus());
        response.setQrCodeUrl(table.getQrCodeUrl());
        response.setIsActive(table.getIsActive());
        response.setCreatedAt(table.getCreatedAt());
        response.setUpdatedAt(table.getUpdatedAt());
        return response;
    }

    @Override
    public List<RestaurantTableResponse> findAll() {
        return List.of();
    }

    @Override
    public RestaurantTableResponse findById(UUID id) {
        return null;
    }

    @Override
    public RestaurantTableResponse update(UUID id, RestaurantTableRequest request) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
