package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.DuplicateResourceException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.menus.Ingredient;
import rmsbackend.dto.menus.ingredient.IngredientRequest;
import rmsbackend.dto.menus.ingredient.IngredientResponse;
import rmsbackend.repository.menus.IngredientRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientResponse createIngredient(IngredientRequest request) {
        log.info("Creating ingredient: {}", request.getName());

        if (ingredientRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Ingredient already exists: " + request.getName()
            );
        }

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .nameKh(request.getNameKh())
                .unit(request.getUnit())
                .description(request.getDescription())
                .stockStatus(request.getStockStatus())
                .build();

        ingredient = ingredientRepository.save(ingredient);

        log.info("Ingredient created successfully: {}", ingredient.getId());

        return mapToResponse(ingredient);
    }


    public Page<IngredientResponse> getAllIngredient(PaginationRequest pagination) {
        log.info("Fetching all ingredients for pagination: {}", pagination);

        Pageable pageable = PaginationUtils.pageable(pagination);

        var results = ingredientRepository.findAll(pageable).map(this::mapToResponse);
        log.info("Retrieved ingredients include pagination on date {}", LocalDateTime.now());

        return results;
    }



    // map response
    public IngredientResponse mapToResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .nameKh(ingredient.getNameKh())
                .unit(ingredient.getUnit())
                .description(ingredient.getDescription())
                .stockStatus(ingredient.getStockStatus())
                .createdAt(ingredient.getCreatedAt())
                .updatedAt(ingredient.getUpdatedAt())
                .build();
    }

}
