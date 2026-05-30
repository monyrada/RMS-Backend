package rmsbackend.repository.menus;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.menus.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {

    List<Item> findByCategoryId(String categoryId);

}
