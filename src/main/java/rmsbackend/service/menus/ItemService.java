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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemResponse create(ItemRequest request) {

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow();

        Item item = Item.builder()
                .id(UUID.randomUUID().toString())
                .categoryId(category.getId())
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        itemRepository.save(item);

        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .build();
    }

    public List<ItemResponse> getAll() {
        return List.of();
    }
}
