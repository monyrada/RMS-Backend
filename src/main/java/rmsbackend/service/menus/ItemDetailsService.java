package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.DuplicateResourceException;
import rmsbackend.common.exception.ResourceNotFoundException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.menus.ItemDetails;
import rmsbackend.dto.menus.itemdetails.ItemDetailRequest;
import rmsbackend.dto.menus.itemdetails.ItemDetailResponse;
import rmsbackend.repository.menus.ItemDetailRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemDetailsService {

    private final ItemDetailRepository itemDetailRepository;

    public ItemDetailResponse createItemDetail(ItemDetailRequest request) {
        log.info("Creating item detail for itemId: {}, ingredientId: {}", request.getItemId(), request.getIngredientId());

        if (itemDetailRepository.existsByItemIdAndIngredientId(request.getItemId(), request.getIngredientId())) {
            throw new DuplicateResourceException("Ingredient already exists for itemID: "+ request.getItemId());
        }

        ItemDetails itemDetails = ItemDetails.builder()
                .itemId(request.getItemId())
                .ingredientId(request.getIngredientId())
                .quantity(request.getQuantity())
                .note(request.getNote())
                .build();

        itemDetails = itemDetailRepository.save(itemDetails);

        return mapToResponse(itemDetails);
    }

    // get item details by id
    public ItemDetailResponse getItemDetailById(String id) {
        ItemDetails itemDetails = itemDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item details not found for id: " + id));

        log.info("Retrieved item details with id: {}", itemDetails.getItemId());

        return mapToResponse(itemDetails);
    }

    // get item details with pagination
    public Page<ItemDetailResponse> getAllItemDetails(PaginationRequest pagination) {
        Pageable pageable = PaginationUtils.pageable(pagination);

        var results = itemDetailRepository.findAll(pageable).map(this::mapToResponse);
        log.info("Retrieved items include pagination on date {}", LocalDateTime.now());

        return results;
    }

    public ItemDetailResponse updateItemDetails(String id, ItemDetailRequest request) {
        log.info("Updating item detail: {}", id);

        ItemDetails itemDetails = itemDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item detail not found with id: " + id));

        itemDetails.setItemId(request.getItemId());
        itemDetails.setIngredientId(request.getIngredientId());
        itemDetails.setQuantity(request.getQuantity());
        itemDetails.setNote(request.getNote());

        itemDetails = itemDetailRepository.save(itemDetails);

        log.info("Item detail updated successfully: {}", id);

        return mapToResponse(itemDetails);
    }

    // delete item detail
    public void deleteItemDetails(String id) {
        log.info("Deleting item detail: {}", id);

        ItemDetails itemDetails = itemDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item detail not found with id: " + id));

        itemDetailRepository.delete(itemDetails);
        log.info("Item detail deleted successfully: {}", id);
    }

    private ItemDetailResponse mapToResponse(
            ItemDetails itemDetails) {

        return ItemDetailResponse.builder()
                .id(itemDetails.getId())
                .itemId(itemDetails.getItemId())
                .ingredientId(itemDetails.getIngredientId())
                .quantity(itemDetails.getQuantity())
                .note(itemDetails.getNote())
                .createdAt(itemDetails.getCreatedAt())
                .updatedAt(itemDetails.getUpdatedAt())
                .build();
    }
}
