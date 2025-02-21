package com.user_microservice.user.application.handler.authenticationhandler;

import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.security.IAuthenticationSecurityPort;
import com.user_microservice.user.domain.usecase.AuthenticationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationHandlerTest {

        @Mock
        private IAuthenticationSecurityPort authenticationSecurityPort;

        @InjectMocks
        private AuthenticationUseCase authenticationUseCase;

        private String email;
        private String password;
        private String token;
        private User user;

        @BeforeEach
        void setUp() {
            email = "user@example.com";
            password = "securePassword";
            token = "mocked-jwt-token";

            user = new User();
            user.setEmail(email);
            user.setPassword(password);
        }

        @Test
        @DisplayName("Given valid credentials, when logging in, then return JWT token")
        void givenValidCredentials_whenLoggingIn_thenReturnJwtToken() {
            when(authenticationSecurityPort.validateCredentials(email, password)).thenReturn(true);
            when(authenticationSecurityPort.authenticate(email, password)).thenReturn(user);
            when(authenticationSecurityPort.generateToken(user)).thenReturn(token);

            String result = authenticationUseCase.login(email, password);

            assertNotNull(result);
            assertEquals(token, result);
            verify(authenticationSecurityPort, times(1)).validateCredentials(email, password);
            verify(authenticationSecurityPort, times(1)).authenticate(email, password);
            verify(authenticationSecurityPort, times(1)).generateToken(user);
        }

}

