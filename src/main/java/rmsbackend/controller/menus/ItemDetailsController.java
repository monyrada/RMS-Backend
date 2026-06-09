package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.domain.menus.Item;
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.menus.itemdetails.ItemDetailRequest;
import rmsbackend.dto.menus.itemdetails.ItemDetailResponse;
import rmsbackend.service.menus.ItemDetailsService;
import rmsbackend.service.menus.ItemService;

@RestController
@RequestMapping("/api/v1/item-details")
@RequiredArgsConstructor
@Tag(name = "ItemDetails APIs", description = "Endpoints for managing item-details.")
public class ItemDetailsController {

    private final ItemDetailsService itemDetailsService;
    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "Create a new item-details", description = "Return a item-details" )
    public RespondDTO create(@RequestBody ItemDetailRequest request) {
        var itemDetails = itemDetailsService.createItemDetail(request);

        return JSONRespond.respond(itemDetails, StatusCode.CREATED, "Item-Details created successfully.");
    }

    @GetMapping
    @Operation(summary = "Get all item details", description = "Return item details data as list")
    @Parameters({
            @Parameter(name = "offset", description = "Records to skip", example = "0"),
            @Parameter(name = "max", description = "Max records to return", example = "10"),
            @Parameter(name = "sort", description = "Field to sort by", example = "id"),
            @Parameter(name = "order", description = "Order to sort by", example = "asc")
    })
    public RespondDTO getAllItemDetails(@Parameter(hidden = true)PaginationRequest pagination) {
        Page<ItemDetailResponse> itemDetailsList = itemDetailsService.getAllItemDetails(pagination);

        if (itemDetailsList.isEmpty()) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Item details not found.");
        }

        return JSONRespond.respond(itemDetailsList, StatusCode.SUCCESS, "Item details returned successfully.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item detail by Id", description = "Return a item detail data.")
    public RespondDTO getItemDetailById(@PathVariable String id) {
        ItemDetailResponse itemDetailList = itemDetailsService.getItemDetailById(id);

        return JSONRespond.respond(itemDetailList, StatusCode.SUCCESS);
    }

    @GetMapping("/item/{itemId}")
    @Operation(summary = "Get Item Details By Item")
    public RespondDTO getByItemId(@PathVariable String itemId) {

        return JSONRespond.respond(itemService.getItemById(itemId), StatusCode.SUCCESS, "Item details retrieved successfully."
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Item Detail")
    public RespondDTO update(@PathVariable String id, @Valid @RequestBody ItemDetailRequest request) {
        ItemDetailResponse response = itemDetailsService.updateItemDetails(id, request);

        return JSONRespond.respond(response, StatusCode.SUCCESS, "Item detail updated successfully.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Item Detail")
    public RespondDTO delete(@PathVariable String id) {
        itemDetailsService.deleteItemDetails(id);

        return JSONRespond.respond(null, StatusCode.SUCCESS, "Item detail deleted successfully.");
    }


}
