package rmsbackend.dto.roles;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class RolePermissionRequest {

    /** Permission codes the role should end up with, e.g. ORDER_VIEW. Replaces the current set. */
    @NotNull
    private Set<String> permissions;

}
