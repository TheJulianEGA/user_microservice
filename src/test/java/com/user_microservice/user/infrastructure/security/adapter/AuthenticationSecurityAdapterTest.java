package com.user_microservice.user.infrastructure.security.adapter;

import com.user_microservice.user.domain.exception.InvalidCredentialsException;
import com.user_microservice.user.domain.exception.NoAuthenticatedUserIdFoundException;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.spi.IRolePersistencePort;
import com.user_microservice.user.domain.util.RoleName;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.mapper.IUserEntityMapper;
import com.user_microservice.user.infrastructure.security.service.JwtService;
import com.user_microservice.user.infrastructure.util.InfrastructureConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationSecurityAdapterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private IRolePersistencePort rolePersistencePort;

    @Mock
    private IUserEntityMapper userEntityMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;


    @InjectMocks
    private AuthenticationSecurityAdapter authenticationSecurityAdapter;

    @Test
    void authenticate_ValidCredentials_ShouldReturnUser() {

        String email = "test@example.com";
        String password = "password";
        UserEntity userEntity = new UserEntity();
        User expectedUser = new User();
        authentication = new UsernamePasswordAuthenticationToken(userEntity, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userEntityMapper.toModel(userEntity)).thenReturn(expectedUser);

        User result = authenticationSecurityAdapter.authenticate(email, password);

        assertNotNull(result);
        assertEquals(expectedUser.getEmail(), result.getEmail());

        verify(authenticationManager,times(1)).authenticate(any());
        verify(userEntityMapper,times(1)).toModel(userEntity);
    }

    @Test
    void authenticate_InvalidCredentials_ShouldThrowException() {

        String email = "test@example.com";
        String password = "wrongpassword";

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class,
                () -> authenticationSecurityAdapter.authenticate(email, password));

        verify(authenticationManager,times(1)).authenticate(any());
    }

    @Test
    void generateToken_ValidUser_ShouldReturnToken() {
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setEmail("test@example.com");

        Role role = new Role();
        role.setName(RoleName.CUSTOMER);
        user.setRole(role);

        String expectedToken = "mocked-jwt-token";

        when(jwtService.generateToken(any(User.class), any())).thenReturn(expectedToken);
        when(rolePersistencePort.getRoleByName(any())).thenReturn(role);

        String result = authenticationSecurityAdapter.generateToken(user);

        assertNotNull(result);
        assertEquals(expectedToken, result);

        verify(jwtService,times(1)).generateToken(any(User.class), any());
        verify(rolePersistencePort,times(1)).getRoleByName(any());
    }

    @Test
    void validateCredentials_ValidCredentials_ShouldReturnTrue() {
        String email = "test@example.com";
        String password = "password";

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        boolean result = authenticationSecurityAdapter.validateCredentials(email, password);

        assertTrue(result);

        verify(authenticationManager,times(1)).authenticate(any());
        verify(authentication,times(1)).isAuthenticated();
    }

    @Test
    void validateCredentials_InvalidCredentials_ShouldThrowException() {

        String email = "test@example.com";
        String password = "wrongpassword";

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class,
                () -> authenticationSecurityAdapter.validateCredentials(email, password));

        verify(authenticationManager,times(1)).authenticate(any());
    }

    @Test
    void getAuthenticatedUserId_ValidUser_ShouldReturnUserId() {

        when(userDetails.getUsername()).thenReturn("1");

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Long userId = authenticationSecurityAdapter.getAuthenticatedUserId();

        assertNotNull(userId);
        assertEquals(1L, userId);

        verify(userDetails,times(1)).getUsername();
        verify(authentication,times(1)).getPrincipal();
        verify(securityContext,times(1)).getAuthentication();
    }

    @Test
    void getAuthenticatedUserId_NoAuthenticatedUser_ShouldThrowException() {

        when(authentication.getPrincipal()).thenReturn("invalidUser");

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        assertThrows(NoAuthenticatedUserIdFoundException.class,
                () -> authenticationSecurityAdapter.getAuthenticatedUserId());

        verify(authentication,times(1)).getPrincipal();
        verify(securityContext,times(1)).getAuthentication();
    }
    @Test
    void authenticate_TooManyFailedAttempts_ShouldBlockAccount() {
        String email = "test@example.com";
        String password = "wrongpassword";

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        for (int i = 0; i < 3; i++) {
            assertThrows(InvalidCredentialsException.class,
                    () -> authenticationSecurityAdapter.authenticate(email, password));
        }

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationSecurityAdapter.authenticate(email, password)
        );

        assertEquals(InfrastructureConstants.ACCOUNT_BLOCKED, exception.getMessage());

        verify(authenticationManager,times(3)).authenticate(any());
    }

}