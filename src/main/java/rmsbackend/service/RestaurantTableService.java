package rmsbackend.service;

import rmsbackend.dto.table.RestaurantTableRequest;
import rmsbackend.dto.table.RestaurantTableResponse;

import java.util.List;
import java.util.UUID;

public interface RestaurantTableService {
    RestaurantTableResponse create(RestaurantTableRequest request);

    List<RestaurantTableResponse> findAll();

    RestaurantTableResponse findById(String id);

    RestaurantTableResponse update(String id, RestaurantTableRequest request);

    void delete(String id);
}
