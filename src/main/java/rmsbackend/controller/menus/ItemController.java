package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.response.ApiResponse;
import rmsbackend.common.generic.response.ResponseBuilder;
import rmsbackend.common.generic.response.StatusCode;
import rmsbackend.dto.menus.item.ItemRequest;
import rmsbackend.dto.menus.item.ItemResponse;
import rmsbackend.service.menus.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Tag(name = "Items APIs", description = "Endpoints for managing items.")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "Create a new item", description = "Create a item")
    public ApiResponse<ItemResponse> createItem(@RequestBody ItemRequest request) {
        ItemResponse item = itemService.create(request);

        return ResponseBuilder.respond(StatusCode.CREATED, item);
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Return item data as list")
    public ApiResponse<List<ItemResponse>> getAllItems() {
        List<ItemResponse> itemsList = itemService.getAllItems();

        if (itemsList.isEmpty()) {
            return ResponseBuilder.respond(StatusCode.NOT_FOUND, "Record not found!");
        }

        return ResponseBuilder.respond(StatusCode.SUCCESS, itemsList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by Id", description = "Return a item data.")
    public ApiResponse<ItemResponse> getItemById(@PathVariable String id) {
        ItemResponse itemResponse = itemService.getItemById(id);

        return ResponseBuilder.respond(StatusCode.SUCCESS, itemResponse);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get all items by categoryId", description = "Return a item listing data.")
    public ApiResponse<List<ItemResponse>> getAllItemsByCategoryId(@PathVariable String categoryId) {
        List<ItemResponse> itemsList = itemService.getAllItemsByCategoryId(categoryId);

        if (itemsList.isEmpty()) {
            return ResponseBuilder.respond(StatusCode.NOT_FOUND, "Record not found!");
        }

        return ResponseBuilder.respond(StatusCode.SUCCESS, itemsList);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a item", description = "Return no content.")
    public ResponseEntity<Void> deleteItemById(@PathVariable String id) {

        itemService.deleteItemById(id);

        return ResponseEntity.noContent().build();
    }

}
