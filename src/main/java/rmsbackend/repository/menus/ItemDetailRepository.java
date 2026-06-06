package rmsbackend.repository.menus;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.menus.ItemDetails;

import java.util.List;

public interface ItemDetailRepository extends JpaRepository<ItemDetails, String> {

    List<ItemDetails> findByItemId(String itemId);

    boolean existsByItemIdAndIngredientId(String itemId, String ingredientId);

}
