package rmsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.table.RestaurantTableFilterRequest;
import rmsbackend.dto.table.RestaurantTableRequest;
import rmsbackend.dto.table.RestaurantTableResponse;
import rmsbackend.dto.table.TableScanResponse;
import rmsbackend.dto.table.TableSummaryResponse;
import rmsbackend.service.tables.RestaurantTableService;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/tables")
@RequiredArgsConstructor
@Tag(name = "Table Management APIs", description = "APIs for managing restaurant tables and QR-based ordering.")
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @PostMapping
    @Operation(summary = "Create a new table", description = "Create a restaurant table and generate its QR code link")
    public RespondDTO createTable(@Valid @RequestBody RestaurantTableRequest request) {
        RestaurantTableResponse table = tableService.create(request);

        return JSONRespond.respond(table, StatusCode.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all tables", description = "Return table data as a paginated list")
    @Parameters({
            @Parameter(name = "offset", description = "Records to skip", example = "0"),
            @Parameter(name = "max",    description = "Max records to return", example = "10"),
            @Parameter(name = "sort",   description = "Field to sort by", example = "tableNumber"),
            @Parameter(name = "order",  description = "Order to sort by", example = "asc"),
            @Parameter(name = "keyword", description = "Search by table number / location"),
            @Parameter(name = "status",  description = "Filter by table status"),
            @Parameter(name = "isActive", description = "Filter by active flag")
    })
    public RespondDTO getAllTables(
            @Parameter(hidden = true) PaginationRequest pagination,
            @Parameter(hidden = true) RestaurantTableFilterRequest filter) {

        Page<RestaurantTableResponse> tables = tableService.getAllTables(pagination, filter);
        if (tables.isEmpty()) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Record not found!");
        }

        return JSONRespond.respond(tables, StatusCode.SUCCESS, "Get record list successfully!");
    }

    @GetMapping("/summary")
    @Operation(summary = "Get table status summary", description = "Return table counts by status for the floor-plan overview cards")
    public RespondDTO getSummary() {
        TableSummaryResponse summary = tableService.getSummary();

        return JSONRespond.respond(summary, StatusCode.SUCCESS, "Get summary successfully.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get table by ID", description = "Return a single table")
    public RespondDTO getTableById(@PathVariable String id) {
        RestaurantTableResponse table = tableService.getTableById(id);

        return JSONRespond.respond(table, StatusCode.SUCCESS, "Get record successfully.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update table by ID", description = "Update a restaurant table")
    public RespondDTO updateTable(@PathVariable String id, @Valid @RequestBody RestaurantTableRequest request) {
        RestaurantTableResponse table = tableService.update(id, request);

        return JSONRespond.respond(table, StatusCode.UPDATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a table", description = "Soft-deletes a table (does not hard-delete, keeps order history intact)")
    public RespondDTO deleteTable(@PathVariable String id) {
        tableService.delete(id);

        return JSONRespond.respond(null, StatusCode.DELETED, "Table deactivated successfully.");
    }

    @GetMapping(value = "/{id}/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Get table QR code image", description = "Return a printable PNG QR code that links to this table's ordering page")
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable String id) {
        byte[] qrCodeImage = tableService.generateQrCodeImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .body(qrCodeImage);
    }

    @GetMapping("/scan/{id}")
    @Operation(summary = "Scan a table's QR code", description = "Public endpoint called by the customer app right after scanning a table QR code; " +
            "validates the table and reports whether ordering can proceed")
    public RespondDTO scanTable(@PathVariable String id) {
        TableScanResponse scanResult = tableService.scanTable(id);

        return JSONRespond.respond(scanResult, StatusCode.SUCCESS, scanResult.getMessage());
    }

}
