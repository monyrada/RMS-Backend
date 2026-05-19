package rmsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rmsbackend.dto.table.RestaurantTableRequest;
import rmsbackend.dto.table.RestaurantTableResponse;
import rmsbackend.service.RestaurantTableService;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@Tag(name = "Table Management APIs", description = "APIs for managing tables.")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new table", description = "Create a restaurant table")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Table created successfully",
                    content = @Content(schema = @Schema(implementation = RestaurantTableResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "409", description = "Table number already exists", content = @Content)
    })
    public RestaurantTableResponse createTable(@Valid @RequestBody RestaurantTableRequest request) {
        return restaurantTableService.create(request);
    }

    @GetMapping
    @Operation(summary = "Get all table data", description = "Get all restaurant table")
    public ResponseEntity<List<RestaurantTableResponse>> findAll() {
        return ResponseEntity.ok(restaurantTableService.findAll());
    }



}
