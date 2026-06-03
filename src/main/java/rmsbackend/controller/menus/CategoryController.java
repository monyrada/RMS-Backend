package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.ApiResponse;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.generic.ResponseBuilder;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.menus.category.CategoryRequest;
import rmsbackend.dto.menus.category.CategoryResponse;
import rmsbackend.service.menus.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category APIs", description = "Endpoints for managing categories.")
public class CategoryController {

    final private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a category")
    public RespondDTO createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);

        return JSONRespond.respond(category, StatusCode.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category by ID", description = "Return category list data.")
    public RespondDTO updateCategory(
            @PathVariable String id,
            @RequestBody CategoryRequest request
    ) {
        CategoryResponse category = categoryService.updateCategory(id, request);

        return JSONRespond.respond(category, StatusCode.UPDATED);
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve paginated categories")
    @Parameters({
            @Parameter(name = "offset", description = "Records to skip", example = "0"),
            @Parameter(name = "max", description = "Max records to return", example = "10"),
            @Parameter(name = "sort", description = "Field to sort by", example = "id"),
            @Parameter(name = "order", description = "Order to sort by", example = "asc")
    })
    public RespondDTO getAllCategories(@Parameter(hidden = true) PaginationRequest pagination) {
        Page<CategoryResponse> categories = categoryService.getAllCategories(pagination);

        if (categories.isEmpty()) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Record not found!");
        }

        return JSONRespond.respond(categories, StatusCode.SUCCESS, "Get data successfully!");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Get category by ID")
    public RespondDTO getCategoryById(@PathVariable String id) {
        CategoryResponse response = categoryService.getCategoryById(id);

        return JSONRespond.respond(response, StatusCode.SUCCESS);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by ID", description = "Delete category by ID")
    public RespondDTO deleteCategoryById(@PathVariable String id) {
        categoryService.deleteCategory(id);

        return JSONRespond.respond(null, StatusCode.DELETED, "Category deleted successfully.");
    }

}
