package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.menus.Category;
import rmsbackend.domain.menus.Item;
import rmsbackend.dto.menus.item.ItemRequest;
import rmsbackend.dto.menus.item.ItemResponse;
import rmsbackend.repository.menus.CategoryRepository;
import rmsbackend.repository.menus.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemResponse create(ItemRequest request) {

        // find category data by categoryId
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id:" + request.getCategoryId()));

        Item item = Item.builder()
                .categoryId(category.getId())
                .name(request.getName())
                .nameKh(request.getNameKh())
                .price(request.getPrice())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        itemRepository.save(item);

        return mapToResponse(item);
    }

    public List<ItemResponse> getAllItemsByCategoryId(String categoryId) {

        return itemRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ItemResponse getItemById(String id) {
        Item item = itemRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id:" + id));

        return mapToResponse(item);
    }

    public ItemResponse updateItemById(String id, ItemRequest request) {
        Item item = itemRepository
                .findById(id)
                .orElseThrow();

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow();

        item.setCategoryId(category.getId());
        item.setName(request.getName());
        item.setNameKh(request.getNameKh());
        item.setPrice(request.getPrice());
        item.setDescription(request.getDescription());

        return mapToResponse(itemRepository.save(item));
    }

    public Page<ItemResponse> getAllItems(PaginationRequest pagination) {
        Pageable pageable = PaginationUtils.pageable(pagination);

        return itemRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public void deleteItemById(String id) {
        itemRepository.deleteById(id);
    }

    private ItemResponse mapToResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .categoryId(item.getCategoryId())
                .name(item.getName())
                .nameKh(item.getNameKh())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .description(item.getDescription())
                .build();
    }

}
