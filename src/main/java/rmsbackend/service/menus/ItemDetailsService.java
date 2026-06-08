package rmsbackend.service.menus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.DuplicateResourceException;
import rmsbackend.domain.menus.ItemDetails;
import rmsbackend.dto.menus.itemdetails.ItemDetailRequest;
import rmsbackend.dto.menus.itemdetails.ItemDetailResponse;
import rmsbackend.repository.menus.ItemDetailRepository;

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
