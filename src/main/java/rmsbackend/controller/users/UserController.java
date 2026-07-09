package rmsbackend.controller.users;

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
import rmsbackend.dto.users.UserRequest;
import rmsbackend.dto.users.UserResponse;
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

}
