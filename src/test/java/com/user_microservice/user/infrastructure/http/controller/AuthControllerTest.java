package com.user_microservice.user.infrastructure.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_microservice.user.application.dto.authenticationdto.AuthenticationRequest;
import com.user_microservice.user.application.dto.authenticationdto.AuthenticationResponse;
import com.user_microservice.user.application.handler.authenticationhandler.IAuthenticationHandler;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAuthenticationHandler authenticationHandler;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("julian@mail.com");
        authenticationRequest.setPassword("123");

        authenticationResponse = new AuthenticationResponse();
    }

    @Test
    void login_ValidRequest_ShouldReturnOkResponse() throws Exception {


        when(authenticationHandler.login(any(AuthenticationRequest.class))).thenReturn(authenticationResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(authenticationResponse)));

        verify(authenticationHandler, times(1)).login(any(AuthenticationRequest.class));
    }

}