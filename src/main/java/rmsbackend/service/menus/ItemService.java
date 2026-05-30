package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public ItemResponse getItemById(String id) {
        Item item = itemRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id:" + id));

        return mapToResponse(item);
    }

    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
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
