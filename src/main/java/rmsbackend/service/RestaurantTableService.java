package rmsbackend.service;

import rmsbackend.dto.table.RestaurantTableRequest;
import rmsbackend.dto.table.RestaurantTableResponse;

import java.util.List;
import java.util.UUID;

public interface RestaurantTableService {
    RestaurantTableResponse create(RestaurantTableRequest request);

    List<RestaurantTableResponse> findAll();

    RestaurantTableResponse findById(UUID id);

    RestaurantTableResponse update(UUID id, RestaurantTableRequest request);

    void delete(UUID id);
}
