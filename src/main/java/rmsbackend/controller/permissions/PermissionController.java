package rmsbackend.controller.permissions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.common.util.JSONRespond;
import rmsbackend.dto.RespondDTO;
import rmsbackend.dto.permissions.PermissionResponse;
import rmsbackend.service.permissions.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions APIs", description = "Endpoints for listing permissions.")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Return every permission that can be granted to a role")
    public RespondDTO getAllPermissions() {
        List<PermissionResponse> permissions = permissionService.getAll();

        return JSONRespond.respond(permissions, StatusCode.SUCCESS);
    }
}
