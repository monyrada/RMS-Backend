package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category by ID", description = "Return category list data.")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id,
            @RequestBody CategoryRequest request)
    {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Get category by ID")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by ID", description = "Delete category by ID")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable String id) {
        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }

}
