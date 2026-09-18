package rmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rmsbackend.domain.RestaurantTable;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, String>, JpaSpecificationExecutor<RestaurantTable> {
    boolean existsByTableNumber(String tableNumber);
    boolean existsByTableNumberAndIdNot(String tableNumber, String id);
}
