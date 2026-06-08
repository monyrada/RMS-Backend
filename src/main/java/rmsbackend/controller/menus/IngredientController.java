package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.PaginationRequest;
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

    @GetMapping
    @Operation(summary = "Get all ingredients", description = "Return ingredients data as list")
    @Parameters({
            @Parameter(name = "offset", description = "Records to skip", example = "0"),
            @Parameter(name = "max", description = "Max records to return", example = "10"),
            @Parameter(name = "sort", description = "Field to sort by", example = "id"),
            @Parameter(name = "order", description = "Order to sort by", example = "asc")
    })
    public RespondDTO getAll(@Parameter(hidden = true) PaginationRequest pagination) {
        Page<IngredientResponse> ingredientList = ingredientService.getAllIngredient(pagination);

        if (ingredientList.isEmpty()) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Ingredient not found.");
        }

        return JSONRespond.respond(ingredientList, StatusCode.SUCCESS, "Ingredient list returned successfully.");

    }


}
