package rmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.RestaurantTable;

import java.util.UUID;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, String> {
    boolean existsByTableNumber(String tableNumber);
}
