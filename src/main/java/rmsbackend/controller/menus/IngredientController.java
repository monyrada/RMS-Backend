package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.menus.ingredient.IngredientRequest;
import rmsbackend.dto.menus.ingredient.IngredientResponse;
import rmsbackend.service.menus.IngredientService;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients APIs", description = "Endpoints for managing ingredients.")
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    @Operation(summary = "Create a new ingredients", description = "return a ingredient" )
    public RespondDTO create(@RequestBody IngredientRequest request) {
        IngredientResponse ingredient = ingredientService.createIngredient(request);

        return JSONRespond.respond(ingredient, StatusCode.CREATED, "Ingredient created successfully.");
    }


}
