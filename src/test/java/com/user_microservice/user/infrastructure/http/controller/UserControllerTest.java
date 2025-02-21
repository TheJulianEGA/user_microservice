package com.user_microservice.user.infrastructure.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.user_microservice.user.application.dto.userdto.UserRequest;
import com.user_microservice.user.application.dto.userdto.UserResponse;
import com.user_microservice.user.application.handler.userhandler.IUserHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUserHandler userHandler;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        userRequest = new UserRequest();
        userRequest.setName("Juana");
        userRequest.setLastName("Lopez");
        userRequest.setDocumentNumber("123456789");
        userRequest.setBirthDate(LocalDate.of(2000, 5, 15));
        userRequest.setPhone("+573001234567");
        userRequest.setEmail("juana.lopez@example.com");
        userRequest.setPassword("password123");
        userRequest.setRole("ADMINISTRATOR");

        userResponse = new UserResponse();
    }

    @Test
    @DisplayName("Given valid user data, when registering user, then return created user")
    void givenValidUserData_whenRegisteringUser_thenReturnCreatedUser() throws Exception {

        when(userHandler.registerUser(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userResponse)));

        verify(userHandler, times(1)).registerUser(any(UserRequest.class));
    }
}