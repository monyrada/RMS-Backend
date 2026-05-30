package rmsbackend.repository.menus;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.menus.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

}
