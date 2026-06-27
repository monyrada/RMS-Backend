package rmsbackend.repository.menus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rmsbackend.domain.menus.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String>, JpaSpecificationExecutor<Item> {

    List<Item> findByCategoryId(String categoryId);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, String id);

}
