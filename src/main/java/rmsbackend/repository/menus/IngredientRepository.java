package rmsbackend.repository.menus;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.menus.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, String> {
}
