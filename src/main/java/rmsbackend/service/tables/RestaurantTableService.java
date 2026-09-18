package rmsbackend.service.tables;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import rmsbackend.common.exception.BusinessException;
import rmsbackend.common.exception.DuplicateResourceException;
import rmsbackend.common.exception.ResourceNotFoundException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.common.util.QrCodeGenerator;
import rmsbackend.domain.RestaurantTable;
import rmsbackend.dto.table.RestaurantTableFilterRequest;
import rmsbackend.dto.table.RestaurantTableRequest;
import rmsbackend.dto.table.RestaurantTableResponse;
import rmsbackend.dto.table.TableScanResponse;
import rmsbackend.dto.table.TableSummaryResponse;
import rmsbackend.enums.TableStatus;
import rmsbackend.enums.orders.OrderStatus;
import rmsbackend.mapper.RestaurantTableMapper;
import rmsbackend.repository.RestaurantTableRepository;
import rmsbackend.repository.orders.OrderRepository;
import rmsbackend.specification.RestaurantTableSpecification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;
    private final OrderRepository orderRepository;
    private final RestaurantTableMapper tableMapper;

    private static final List<OrderStatus> ACTIVE_ORDER_STATUSES = List.of(
            OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PREPARING,
            OrderStatus.READY, OrderStatus.SERVED
    );

    @Value("${app.qr.base-url}")
    private String qrBaseUrl;

    @Value("${app.qr.image-size:320}")
    private int qrImageSize;

    public RestaurantTableResponse create(RestaurantTableRequest request) {
        String tableNumber = request.getTableNumber().trim();
        if (tableRepository.existsByTableNumber(tableNumber)) {
            throw new DuplicateResourceException("Table number already exists.");
        }

        RestaurantTable table = tableMapper.toEntity(request);
        table = tableRepository.save(table);

        table.setQrCodeUrl(buildQrUrl(table.getId()));
        table = tableRepository.save(table);

        log.info("Table {} created with id {}", table.getTableNumber(), table.getId());
        return tableMapper.toResponse(table);
    }

    public Page<RestaurantTableResponse> getAllTables(PaginationRequest pagination, RestaurantTableFilterRequest filter) {
        Pageable pageable = PaginationUtils.pageable(pagination);
        Specification<RestaurantTable> specification = RestaurantTableSpecification.withFilter(filter);

        return tableRepository.findAll(specification, pageable).map(tableMapper::toResponse);
    }

    /**
     * Table counts by status, for the floor-plan summary cards (Available / Occupied / Reserved / Cleaning).
     */
    public TableSummaryResponse getSummary() {
        Map<TableStatus, Long> counts = tableRepository.findAll().stream()
                .collect(Collectors.groupingBy(RestaurantTable::getStatus, Collectors.counting()));

        return TableSummaryResponse.builder()
                .totalTables(counts.values().stream().mapToLong(Long::longValue).sum())
                .available(counts.getOrDefault(TableStatus.AVAILABLE, 0L))
                .occupied(counts.getOrDefault(TableStatus.OCCUPIED, 0L))
                .reserved(counts.getOrDefault(TableStatus.RESERVED, 0L))
                .cleaning(counts.getOrDefault(TableStatus.CLEANING, 0L))
                .inactive(counts.getOrDefault(TableStatus.INACTIVE, 0L))
                .build();
    }

    public RestaurantTableResponse getTableById(String id) {
        RestaurantTable table = findTableOrThrow(id);
        return tableMapper.toResponse(table);
    }

    public RestaurantTableResponse update(String id, RestaurantTableRequest request) {
        RestaurantTable table = findTableOrThrow(id);

        String tableNumber = request.getTableNumber().trim();
        if (tableRepository.existsByTableNumberAndIdNot(tableNumber, id)) {
            throw new DuplicateResourceException("Table number already exists.");
        }

        tableMapper.updateEntity(table, request);
        table = tableRepository.save(table);

        log.info("Table {} updated", table.getTableNumber());
        return tableMapper.toResponse(table);
    }

    public void delete(String id) {
        RestaurantTable table = findTableOrThrow(id);

        if (orderRepository.existsByTableIdAndStatusIn(id, ACTIVE_ORDER_STATUSES)) {
            throw new BusinessException("Cannot deactivate a table that has active orders.");
        }

        table.setIsActive(false);
        table.setStatus(TableStatus.INACTIVE);
        tableRepository.save(table);

        log.info("Table {} deactivated", table.getTableNumber());
    }

    /**
     * Called by the customer app right after scanning a table's QR code.
     * Validates the table is real and tells the client whether ordering can proceed.
     */
    public TableScanResponse scanTable(String id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR code: table not found."));

        if (!Boolean.TRUE.equals(table.getIsActive()) || table.getStatus() == TableStatus.INACTIVE) {
            return TableScanResponse.builder()
                    .id(table.getId())
                    .tableNumber(table.getTableNumber())
                    .capacity(table.getCapacity())
                    .location(table.getLocation())
                    .status(table.getStatus())
                    .orderingEnabled(false)
                    .message("This table is currently inactive. Please ask staff for assistance.")
                    .build();
        }

        boolean orderingEnabled = table.getStatus() == TableStatus.AVAILABLE
                || table.getStatus() == TableStatus.OCCUPIED;

        String message = switch (table.getStatus()) {
            case AVAILABLE -> "Table is ready. You can start ordering.";
            case OCCUPIED -> "Table is already in use. You can add to the current order.";
            case RESERVED -> "This table is reserved. Please check with staff.";
            case CLEANING -> "This table is being cleaned. Please wait a moment.";
            case INACTIVE -> "This table is currently unavailable.";
        };

        log.info("Table {} scanned", table.getTableNumber());

        return TableScanResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .location(table.getLocation())
                .status(table.getStatus())
                .orderingEnabled(orderingEnabled)
                .message(message)
                .build();
    }

    /**
     * Renders the printable QR code image (PNG) that, once scanned, resolves to this table.
     */
    public byte[] generateQrCodeImage(String id) {
        RestaurantTable table = findTableOrThrow(id);
        return QrCodeGenerator.generatePng(table.getQrCodeUrl(), qrImageSize);
    }

    private RestaurantTable findTableOrThrow(String id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id:" + id));
    }

    private String buildQrUrl(String tableId) {
        return UriComponentsBuilder.fromUriString(qrBaseUrl)
                .path("/menu")
                .queryParam("tableId", tableId)
                .toUriString();
    }
}
