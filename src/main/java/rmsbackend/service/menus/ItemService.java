package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.DuplicateResourceException;
import rmsbackend.common.exception.ResourceNotFoundException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.menus.Category;
import rmsbackend.domain.menus.Item;
import rmsbackend.dto.menus.item.ItemFilterRequest;
import rmsbackend.dto.menus.item.ItemRequest;
import rmsbackend.dto.menus.item.ItemResponse;
import rmsbackend.repository.menus.CategoryRepository;
import rmsbackend.repository.menus.ItemRepository;
import rmsbackend.specification.ItemSpecification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemResponse create(ItemRequest request) {

        // check name should be unique
        if (itemRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Item name already exists");
        }

        // find category data by categoryId
        Category category = categoryRepository
                .findById(request.getCategoryId()).orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id:" + request.getCategoryId()));

        Item item = Item.builder()
                .categoryId(category.getId())
                .name(request.getName())
                .nameKh(request.getNameKh())
                .price(request.getPrice())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        itemRepository.save(item);
        log.info("Item id {} has been created on date {}", item.getId(), item.getCreatedAt());

        return mapToResponse(item);
    }

    public List<ItemResponse> getAllItemsByCategoryId(String categoryId) {
        log.info("Fetching items by category id {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        List<ItemResponse> items = itemRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        log.info( "Retrieved {} items for category: {}", items.size(), category.getName());
        return items;
    }

    public ItemResponse getItemById(String id) {
        Item item = itemRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id :" + id));

        log.info( "Retrieved item by id: {}", item.getId());
        return mapToResponse(item);
    }

    public ItemResponse updateItemById(String id, ItemRequest request) {
        Item item = itemRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id :" + id));

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id:" + request.getCategoryId()));

        item.setCategoryId(category.getId());
        item.setName(request.getName());
        item.setNameKh(request.getNameKh());
        item.setPrice(request.getPrice());
        item.setDescription(request.getDescription());

        var itemData = itemRepository.save(item);
        log.info( "Retrieved items for category: {}", item.getName());

        return mapToResponse(itemData);
    }

    public Page<ItemResponse> getAllItems(PaginationRequest pagination, ItemFilterRequest filter) {
        log.info("Fetching items include pagination on date {}", LocalDateTime.now());

        Pageable pageable = PaginationUtils.pageable(pagination);
        Specification<Item> specification = ItemSpecification.withFilter(filter);

        Page<Item> items = itemRepository.findAll(specification, pageable);

        Set<String> categoryIds = items.stream()
                .map(Item::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Category> categoryMap = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        return items.map(item -> mapItemData(item, categoryMap));
    }

    public void deleteItemById(String id) {
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Item not found with id:" + id));

        itemRepository.deleteById(id);
        log.info("Item name {} has been deleted!", item.getName());
    }

    private ItemResponse mapItemData(Item item, Map<String, Category> categoryMap) {
        Category category = categoryMap.get(item.getCategoryId());

        return ItemResponse.builder()
                .id(item.getId())
                //.categoryId(item.getCategoryId())
                .categoryName(category != null ? category.getName() : null)
                .name(item.getName())
                .nameKh(item.getNameKh())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .description(item.getDescription())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private ItemResponse mapToResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                //.categoryId(item.getCategoryId())
                .name(item.getName())
                .nameKh(item.getNameKh())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .description(item.getDescription())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

}
