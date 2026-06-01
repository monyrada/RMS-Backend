package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.ApiResponse;
import rmsbackend.common.generic.ResponseBuilder;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.dto.menus.category.CategoryRequest;
import rmsbackend.dto.menus.category.CategoryResponse;
import rmsbackend.service.menus.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category APIs", description = "Endpoints for managing categories.")
public class CategoryController {

    final private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a category")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);

        return ResponseBuilder.respond(StatusCode.CREATED, category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category by ID", description = "Return category list data.")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable String id,
            @RequestBody CategoryRequest request)
    {
        CategoryResponse category = categoryService.updateCategory(id, request);

        return ResponseBuilder.respond(StatusCode.UPDATED, category);
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all categories")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            return ResponseBuilder.respond(StatusCode.NOT_FOUND, "Record not found!");
        }

        return ResponseBuilder.respond(StatusCode.SUCCESS, categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Get category by ID")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable String id) {
        CategoryResponse response = categoryService.getCategoryById(id);

        return ResponseBuilder.respond(StatusCode.SUCCESS, response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by ID", description = "Delete category by ID")
    public ApiResponse<Void> deleteCategoryById(@PathVariable String id) {
        categoryService.deleteCategory(id);

        return ResponseBuilder.respond(StatusCode.DELETED);
    }

}
