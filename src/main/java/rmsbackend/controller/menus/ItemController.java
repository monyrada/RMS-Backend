package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ItemResponse> createItem(@RequestBody ItemRequest request) {
        return ResponseEntity.ok(itemService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Return item data as list")
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by Id", description = "Return a item data.")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable String id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }


}
