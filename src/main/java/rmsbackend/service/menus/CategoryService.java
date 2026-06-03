package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.menus.Category;
import rmsbackend.dto.menus.category.CategoryRequest;
import rmsbackend.dto.menus.category.CategoryResponse;
import rmsbackend.repository.menus.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    final private CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .nameKh(request.getNameKh())
                .code(request.getCode())
                .status(true)
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        categoryRepository.save(category);

        return mapToResponse(category);
    }

    // Mapping for response data
    private CategoryResponse mapToResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .nameKh(category.getNameKh())
                .code(category.getCode())
                .status(category.getStatus())
                .description(category.getDescription())
                .build();
    }

    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow();

        category.setName(request.getName());
        category.setNameKh(request.getNameKh());
        category.setCode(request.getCode());
        category.setStatus(request.getStatus());
        category.setDescription(request.getDescription());

        categoryRepository.save(category);

        return mapToResponse(category);
    }

    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository
                .findById(id)
                .orElseThrow();

        return mapToResponse(category);
    }

//    public List<CategoryResponse> getAllCategories(PaginationRequest pagination) {
//        return categoryRepository.findAll()
//                .stream()
//                .map(this::mapToResponse)
//                .toList();
//    }

    public List<CategoryResponse> getAllCategories(PaginationRequest pagination) {
        Pageable pageable = PaginationUtils.pageable(pagination);

        return categoryRepository
                .findAll(pageable)
                .map(this::mapToResponse)
                .stream()
                .toList();
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}
