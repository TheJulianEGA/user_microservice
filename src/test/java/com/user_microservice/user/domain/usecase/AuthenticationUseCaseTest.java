package com.user_microservice.user.domain.usecase;

import com.user_microservice.user.domain.exception.AuthenticationException;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.security.IAuthenticationSecurityPort;
import com.user_microservice.user.domain.util.DomainConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationUseCaseTest {

    @Mock
    private IAuthenticationSecurityPort authenticationSecurityPort;

    @InjectMocks
    private AuthenticationUseCase authenticationUseCase;

    private String email;
    private String password;
    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        email = "user@example.com";
        password = "securePassword";
        user = new User();
        user.setEmail(email);
        token = "mocked-jwt-token";
    }

    @Test
    void testLogin_Success() {
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

    @Test
    void testLogin_InvalidCredentials() {
        when(authenticationSecurityPort.validateCredentials(email, password)).thenReturn(false);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                authenticationUseCase.login(email, password)
        );

        assertEquals(DomainConstants.INVALID_USER_CREDENTIALS, exception.getMessage());
        verify(authenticationSecurityPort, times(1)).validateCredentials(email, password);
        verify(authenticationSecurityPort, never()).authenticate(anyString(), anyString());
        verify(authenticationSecurityPort, never()).generateToken(any(User.class));
    }
}