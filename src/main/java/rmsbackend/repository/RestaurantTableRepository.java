package rmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.RestaurantTable;

import java.util.UUID;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, UUID> {
    boolean existsByTableNumber(String tableNumber);
}
