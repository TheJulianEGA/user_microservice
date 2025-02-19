package com.user_microservice.user.infrastructure.http.controller;

import com.user_microservice.user.application.dto.userdto.UserRequest;
import com.user_microservice.user.application.dto.userdto.UserResponse;
import com.user_microservice.user.application.handler.IUserHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserHandler userHandler;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {

        UserResponse userResponse = userHandler.registerUser(userRequest);

        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

}
