package rmsbackend.service.orders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.ResourceNotFoundException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.RestaurantTable;
import rmsbackend.domain.menus.Item;
import rmsbackend.domain.orders.Order;
import rmsbackend.domain.orders.OrderItem;
import rmsbackend.dto.order.OrderFilterRequest;
import rmsbackend.dto.order.OrderItemRequest;
import rmsbackend.dto.order.OrderRequest;
import rmsbackend.dto.order.OrderResponse;
import rmsbackend.enums.TableStatus;
import rmsbackend.enums.orders.OrderStatus;
import rmsbackend.mapper.orders.OrderItemMapper;
import rmsbackend.mapper.orders.OrderMapper;
import rmsbackend.repository.RestaurantTableRepository;
import rmsbackend.repository.menus.ItemRepository;
import rmsbackend.repository.orders.OrderItemRepository;
import rmsbackend.repository.orders.OrderRepository;
import rmsbackend.specification.OrderSpecification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING,   Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
            OrderStatus.PREPARING, Set.of(OrderStatus.READY),
            OrderStatus.READY,     Set.of(OrderStatus.SERVED),
            OrderStatus.SERVED,    Set.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    public OrderResponse create(OrderRequest request) throws BadRequestException {
        if (request.getOrderType() == null)
            throw new BadRequestException("Order type is required");

        if (request.getSource() == null)
            throw new BadRequestException("Order source is required");

        if (request.getItems() == null || request.getItems().isEmpty())
            throw new BadRequestException("Order must have at least one item");

        // find table by id
        RestaurantTable table = restaurantTableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id:" + request.getTableId()));

        // mapping order
        Order order = orderMapper.toEntity(request, table);
        order.setOrderNumber(generateOrderNumber());
        order = orderRepository.save(order); //issue where this

        BigDecimal subTotal = BigDecimal.ZERO;
        for (OrderItemRequest itemDto : request.getItems()) {
            if (itemDto.getMenuId() == null) throw new BadRequestException("Menu item is required");
            if (itemDto.getQuantity() == null || itemDto.getQuantity() < 1)
                throw new BadRequestException("Quantity must be at least 1");

            Item item = itemRepository.findById(itemDto.getMenuId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id:" + itemDto.getMenuId()));

            OrderItem orderItem = orderItemMapper.toEntity(itemDto, order, item);
            orderItemRepository.save(orderItem);
            subTotal = subTotal.add(orderItem.getSubtotal());
        }

        order.setSubtotal(subTotal);
        order.setTotalAmount(subTotal.subtract(order.getDiscount()).add(order.getTax()));
        order = orderRepository.save(order);

        // occupy the table
        table.setStatus(TableStatus.OCCUPIED);
        restaurantTableRepository.save(table);

        log.info("Order {} created for table {}", order.getOrderNumber(), table.getId());
        return loadFullResponse(order.getId());
    }

    public void cancelOrder(String id) throws BadRequestException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + id));

        validateTransition(order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        freeTableIfNoActiveOrders(order.getTable().getId());
        log.info("Order {} cancelled", order.getOrderNumber());
    }

    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + id));

        return orderMapper.toResponse(order);
    }

    public OrderResponse update(String id, OrderRequest request) throws BadRequestException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + id));

        if (request.getStatus() != null && request.getStatus() != order.getStatus()) {
            validateTransition(order.getStatus(), request.getStatus());
            order.setStatus(request.getStatus());

            if (request.getStatus() == OrderStatus.CONFIRMED || request.getStatus() == OrderStatus.CANCELLED) {
                freeTableIfNoActiveOrders(order.getTable().getId());
            }
        }
        if (request.getGuestCount() != null) order.setGuestCount(request.getGuestCount());
        if (request.getNote() != null) order.setNote(request.getNote());

        order = orderRepository.save(order);
        log.info("Order {} updated", order.getOrderNumber());

        return orderMapper.toResponse(order);
    }

    public OrderResponse addNewItem(String orderId, OrderItemRequest request) throws BadRequestException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + orderId));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Cannot add items once the order is being prepared");
        }

        if (request.getMenuId() == null) throw new BadRequestException("Menu item is required!");
        if (request.getQuantity() == null || request.getQuantity() < 1)
            throw new BadRequestException("Quantity must be at least 1");

        Item menu = itemRepository.findById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id:" + request.getMenuId()));

        OrderItem orderItem = orderItemMapper.toEntity(request, order, menu);
        orderItemRepository.save(orderItem);

        recalculateTotals(order);
        log.info("Order {} added", order.getOrderNumber());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse updateExistingItem(String orderId, String itemId, OrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + orderId));

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id:" + itemId));

        if (request.getStatus() != null) item.setStatus(request.getStatus());
        if (request.getNote() != null) item.setNote(request.getNote());

        orderItemRepository.save(item);
        recalculateTotals(order);
        log.info("Order item {} updated", order.getOrderNumber());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse removeItem(String orderId, String itemId) throws BadRequestException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + orderId));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Cannot remove items once the order is being prepared.");
        }

        orderItemRepository.deleteById(itemId);
        recalculateTotals(order);
        log.info("Order item {} removed", order.getOrderNumber());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    public Page<OrderResponse> getAllOrders(PaginationRequest pagination, OrderFilterRequest filter) {
        Pageable pageable = PaginationUtils.pageable(pagination);
        Specification<Order> specification = OrderSpecification.withFilter(filter);

        Page<Order> orders = orderRepository.findAll(specification, pageable);
        return orders.map(orderMapper::toResponse);
    }

    private void validateTransition(OrderStatus from, OrderStatus to) throws BadRequestException {
        if (!ALLOWED_TRANSITIONS.getOrDefault(from, Set.of()).contains(to)) {
            throw new BadRequestException("Cannot change order status from " + from + " to " + to);
        }
    }

    private void recalculateTotals(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        BigDecimal subTotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setSubtotal(subTotal);
        order.setTotalAmount(subTotal.subtract(order.getDiscount()).add(order.getTax()));
    }

    private void freeTableIfNoActiveOrders(String tableId) {
        boolean stillActive = orderRepository.existsByTableIdAndStatusIn(
                tableId, List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED,
                        OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.SERVED)
        );

        if (!stillActive) {
            RestaurantTable table = restaurantTableRepository.findById(tableId)
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found with id:" + tableId));
            table.setStatus(TableStatus.AVAILABLE);
            restaurantTableRepository.save(table);
        }
    }

    private OrderResponse loadFullResponse(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + orderId));

        return orderMapper.toResponse(order);
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long countToday = orderRepository.count() + 1;

        return "ORD-" + datePart + "-" + String.format("%04d", countToday);
    }

}