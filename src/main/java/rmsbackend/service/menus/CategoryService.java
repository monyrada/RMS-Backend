package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.DuplicateResourceException;
import rmsbackend.common.exception.ResourceNotFoundException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.menus.Category;
import rmsbackend.dto.menus.category.CategoryRequest;
import rmsbackend.dto.menus.category.CategoryResponse;
import rmsbackend.repository.menus.CategoryRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    final private CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category name already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .nameKh(request.getNameKh())
                .code(request.getCode())
                .status(true)
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        categoryRepository.save(category);
        log.info("Category id {} has been deleted at created {}", category.getId(), category.getCreatedAt());

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
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category not found."));

        category.setName(request.getName());
        category.setNameKh(request.getNameKh());
        category.setCode(request.getCode());
        category.setStatus(request.getStatus());
        category.setDescription(request.getDescription());

        categoryRepository.save(category);
        log.info("Category id {} has been updated!", category.getId());

        return mapToResponse(category);
    }

    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        log.info("Get category by id {} successfully.", category.getId());
        return mapToResponse(category);
    }

    public Page<CategoryResponse> getAllCategories(PaginationRequest pagination) {
        Pageable pageable = PaginationUtils.pageable(pagination);

        var results = categoryRepository.findAll(pageable).map(this::mapToResponse);

        log.info("Query categories completed on date {}", LocalDateTime.now());
        return results;
    }

    public void deleteCategory(String id) {
        Category category = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        categoryRepository.delete(category);
        log.info("Category name {} has been deleted!", category.getName());
    }
}
