package rmsbackend.controller.users;

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
import rmsbackend.dto.users.*;
import rmsbackend.service.users.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users APIs", description = "Endpoints for managing users.")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "API for crate a user")
    public RespondDTO createUser(@RequestBody UserRequest request) {
        UserResponse user = userService.create(request);

        return JSONRespond.respond(user, StatusCode.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all Users", description = "Return user data as list")
    @Parameters({
            @Parameter(name = "offset",     description = "Records to skip", example = "0"),
            @Parameter(name = "max",        description = "Max records to return", example = "10"),
            @Parameter(name = "sort",       description = "Field to sort by", example = "id"),
            @Parameter(name = "order",      description = "Order to sort by", example = "asc"),
    })
    public RespondDTO getAllItems(@Parameter(hidden = true) PaginationRequest pagination) {
        Page<UserResponse> userList = userService.getAllUsers(pagination);

        if (userList.isEmpty()) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "Record not found!");
        }

        return JSONRespond.respond(userList, StatusCode.SUCCESS);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Update an existing user by its UUID")
    public RespondDTO updateUser(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        UserResponse user = userService.update(id, request);

        if (user == null) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "User not found!");
        }

        return JSONRespond.respond(user, StatusCode.SUCCESS);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Return a single user by its UUID")
    public RespondDTO getUserById(@PathVariable String id) {
        UserResponse user = userService.getUserById(id);

        if (user == null) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "User not found!");
        }

        return JSONRespond.respond(user, StatusCode.SUCCESS);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Delete an existing user by its UUID")
    public RespondDTO deleteUserById(@PathVariable String id) {
        boolean deleted = userService.delete(id);

        if (!deleted) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "User not found!");
        }

        return JSONRespond.respond(null, StatusCode.SUCCESS, "User deleted successfully!");
    }

    @PutMapping("/{id}/change-password")
    @Operation(summary = "Change password", description = "Change password for an authenticated user")
    public RespondDTO changePassword(@PathVariable String id, @Valid @RequestBody ChangePasswordRequest request) {
        try {
            boolean success = userService.changePassword(id, request);
            if (!success) {
                return JSONRespond.respond(null, StatusCode.NOT_FOUND, "User not found!");
            }
            return JSONRespond.respond(null, StatusCode.SUCCESS, "Password changed successfully!");

        } catch (IllegalArgumentException e) {
            return JSONRespond.respond(null, StatusCode.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/forget-password")
    @Operation(summary = "Forget password", description = "Generate a password reset token for the given email")
    public RespondDTO forgetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        String token = userService.forgetPassword(request);

        if (token == null) {
            return JSONRespond.respond(null, StatusCode.NOT_FOUND, "No account found with that email.");
        }

        return JSONRespond.respond(token, StatusCode.SUCCESS, "Reset token generated.");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using a valid reset token")
    public RespondDTO resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        boolean success = userService.resetPassword(request);

        if (!success) {
            return JSONRespond.respond(null, StatusCode.BAD_REQUEST, "Invalid or expired token.");
        }

        return JSONRespond.respond(null, StatusCode.SUCCESS, "Password reset successfully!");
    }


}
