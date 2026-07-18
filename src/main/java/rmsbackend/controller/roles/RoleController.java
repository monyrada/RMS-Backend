package rmsbackend.controller.roles;

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
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.roles.RoleRequest;
import rmsbackend.dto.roles.RoleResponse;
import rmsbackend.service.roles.RoleService;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles APIs", description = "Endpoints for managing roles.")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role", description = "API for creating a role")
    public RespondDTO createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.create(request);

        return JSONRespond.respond(role, StatusCode.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all Roles", description = "Return role data as list")
    @Parameters({
            @Parameter(name = "name",       description = "Filter by role name (partial match)", example = "admin"),
            @Parameter(name = "enabled",    description = "Filter by enabled status", example = "true"),
            @Parameter(name = "offset",     description = "Records to skip", example = "0"),
            @Parameter(name = "max",        description = "Max records to return", example = "10"),
            @Parameter(name = "sort",       description = "Field to sort by", example = "id"),
            @Parameter(name = "order",      description = "Order to sort by", example = "asc"),
    })
    public RespondDTO getAllItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean enabled,
            @Parameter(hidden = true) PaginationRequest pagination) {

        Page<RoleResponse> roleList = roleService.getAllRoles(name, enabled, pagination);

        if (roleList.isEmpty()) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Record not found!");
        }

        return JSONRespond.respond(roleList, StatusCode.SUCCESS);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a role", description = "Update an existing role by its UUID")
    public RespondDTO updateRole(@PathVariable String id, @Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.update(id, request);

        if (role == null) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Role not found!");
        }

        return JSONRespond.respond(role, StatusCode.SUCCESS);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Return a single role by its UUID")
    public RespondDTO getRoleById(@PathVariable String id) {
        RoleResponse role = roleService.getRoleById(id);

        if (role == null) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Role not found!");
        }

        return JSONRespond.respond(role, StatusCode.SUCCESS);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role", description = "Delete an existing role by its UUID")
    public RespondDTO deleteRoleById(@PathVariable String id) {
        boolean deleted = roleService.delete(id);

        if (!deleted) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Role not found!");
        }

        return JSONRespond.respond(null, StatusCode.SUCCESS, "Role deleted successfully!");
    }
}