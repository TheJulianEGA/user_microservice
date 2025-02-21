package com.user_microservice.user.infrastructure.http.controller;

import com.user_microservice.user.application.dto.userdto.UserRequest;
import com.user_microservice.user.application.dto.userdto.UserResponse;
import com.user_microservice.user.application.handler.userhandler.IUserHandler;
import com.user_microservice.user.infrastructure.util.InfrastructureConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserHandler userHandler;

    @Operation(
            summary = "Create a new user",
            description = "Registers a new user in the system and returns the created user details.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PreAuthorize(InfrastructureConstants.ROLE_ADMINISTRATOR + " or " + InfrastructureConstants.ROLE_OWNER )
    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {

        UserResponse userResponse = userHandler.registerUser(userRequest);

        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Check if a user exists with role OWNER",
            description = "Returns true if a user with the given ID and role OWNER exists, false otherwise."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User existence checked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exists_user_owner/{userId}")
    public ResponseEntity<Boolean> existsUserWithOwnerRole(@PathVariable Long userId) {
        boolean exists = userHandler.existsUserWithOwnerRole(userId);
        return ResponseEntity.ok(exists);
    }

}
