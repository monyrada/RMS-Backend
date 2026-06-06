package rmsbackend.controller.menus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.menus.itemdetails.ItemDetailRequest;
import rmsbackend.service.menus.ItemDetailsService;

@RestController
@RequestMapping("/api/v1/item-details")
@RequiredArgsConstructor
@Tag(name = "ItemDetails APIs", description = "Endpoints for managing item-details.")
public class ItemDetailsController {

    private final ItemDetailsService itemDetailsService;

    @PostMapping
    @Operation(summary = "Create a new item-details", description = "Create a item-details" )
    public RespondDTO create(@RequestBody ItemDetailRequest request) {
        var itemDetails = itemDetailsService.createItemDetail(request);

        return JSONRespond.respond(itemDetails, StatusCode.CREATED, "Item Details created successfully.");
    }


}
