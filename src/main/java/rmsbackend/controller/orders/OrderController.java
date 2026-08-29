package rmsbackend.controller.orders;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.order.OrderFilterRequest;
import rmsbackend.dto.order.OrderItemRequest;
import rmsbackend.dto.order.OrderRequest;
import rmsbackend.dto.order.OrderResponse;
import rmsbackend.service.orders.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders APIs", description = "Endpoints for managing dine-in orders.")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Create a dine-in order with its items")
    public RespondDTO createOrder(@RequestBody OrderRequest request) throws BadRequestException {
        OrderResponse order = orderService.create(request);

        return JSONRespond.respond(order, StatusCode.CREATED);
    }

    @PostMapping("/{orderId}/items")
    @Operation(summary = "Add an item to an order")
    public RespondDTO addNewItem(@PathVariable String orderId, @RequestBody OrderItemRequest request) throws BadRequestException {
        OrderResponse order = orderService.addNewItem(orderId, request);

        return JSONRespond.respond(order, StatusCode.CREATED);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Update an order item", description = "Update quantity, status, or note of a line item")
    public RespondDTO updateItem(@PathVariable String orderId, @PathVariable String itemId,
                                 @RequestBody OrderItemRequest request) {
        OrderResponse order = orderService.updateExistingItem(orderId, itemId, request);

        return JSONRespond.respond(order, StatusCode.UPDATED);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Remove an item from an order")
    public RespondDTO removeItem(@PathVariable String orderId, @PathVariable String itemId) throws BadRequestException {
        OrderResponse order = orderService.removeItem(orderId, itemId);

        return JSONRespond.respond(order, StatusCode.DELETED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by Id", description = "Return a single order with its items")
    public RespondDTO getOrderById(@PathVariable String id) {
        OrderResponse order = orderService.getOrderById(id);

        return JSONRespond.respond(order, StatusCode.SUCCESS, "Get record successfully.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Get order by Id", description = "Return a single order with its items")
    public RespondDTO updateOrderById(@PathVariable String id, @RequestBody OrderRequest request) throws BadRequestException {
        OrderResponse order = orderService.update(id, request);

        return JSONRespond.respond(order, StatusCode.UPDATED);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Return order data as a paginated list")
    @Parameters({
            @Parameter(name = "offset", description = "Records to skip", example = "0"),
            @Parameter(name = "max",    description = "Max records to return", example = "10"),
            @Parameter(name = "sort",   description = "Field to sort by", example = "createdAt"),
            @Parameter(name = "order",  description = "Order to sort by", example = "desc"),
            @Parameter(name = "tableId", description = "Filter by table UUID"),
            @Parameter(name = "status",  description = "Filter by order status"),
            @Parameter(name = "source",  description = "Filter by order source (STAFF/CUSTOMER_QR)")
    })
    public RespondDTO getAllOrders(
            @Parameter(hidden = true) PaginationRequest pagination,
            @Parameter(hidden = true) OrderFilterRequest filter ) {

        Page<OrderResponse> orders = orderService.getAllOrders(pagination, filter);
        if (orders.isEmpty()) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Record not found!");
        }

        return JSONRespond.respond(orders, StatusCode.SUCCESS, "Get record list successfully!");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an order", description = "Soft-cancels an order (does not hard-delete)")
    public RespondDTO cancelOrder(@PathVariable String id) throws BadRequestException {
        orderService.cancelOrder(id);

        return JSONRespond.respond(null, StatusCode.DELETED, "Order cancelled!");
    }


}
