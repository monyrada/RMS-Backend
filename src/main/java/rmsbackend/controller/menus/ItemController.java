package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;
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
    public RespondDTO createItem(@RequestBody ItemRequest request) {
        ItemResponse item = itemService.create(request);

        return JSONRespond.respond(item, StatusCode.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Return item data as list")
    @Parameters({
            @Parameter(name = "offset", description = "Records to skip", example = "0"),
            @Parameter(name = "max", description = "Max records to return", example = "10"),
            @Parameter(name = "sort", description = "Field to sort by", example = "id"),
            @Parameter(name = "order", description = "Order to sort by", example = "asc")
    })
    public RespondDTO getAllItems(@Parameter(hidden = true)PaginationRequest pagination) {
        Page<ItemResponse> itemsList = itemService.getAllItems(pagination);

        if (itemsList.isEmpty()) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Record not found!");
        }

        return JSONRespond.respond(itemsList, StatusCode.SUCCESS);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by Id", description = "Return a item data.")
    public RespondDTO getItemById(@PathVariable String id) {
        ItemResponse itemResponse = itemService.getItemById(id);

        return JSONRespond.respond(itemResponse, StatusCode.SUCCESS);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get all items by categoryId", description = "Return a item listing data.")
    public RespondDTO getAllItemsByCategoryId(@PathVariable String categoryId) {
        List<ItemResponse> itemsList = itemService.getAllItemsByCategoryId(categoryId);

        if (itemsList.isEmpty()) {
            return JSONRespond.respond(null ,StatusCode.NOT_FOUND, "Record not found!");
        }

        return JSONRespond.respond(itemsList, StatusCode.SUCCESS);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a item", description = "Return no content.")
    public RespondDTO deleteItemById(@PathVariable String id) {

        itemService.deleteItemById(id);

        return JSONRespond.respond(null, StatusCode.DELETED);
    }

}
