package com.user_microservice.user.infrastructure.security.filter;

import com.user_microservice.user.domain.util.RoleName;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.RoleEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.repository.IUserRepository;
import com.user_microservice.user.infrastructure.security.service.JwtService;
import com.user_microservice.user.infrastructure.util.InfrastructureConstants;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private IUserRepository userRepository;
    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String VALID_TOKEN = "Bearer valid-token";
    private static final String EXPIRED_TOKEN = "Bearer expired-token";
    private static final String INVALID_TOKEN = "Bearer invalid-token";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoAuthHeader_ShouldProceedWithoutAuthentication() throws Exception {
        when(request.getHeader(("Authorization"))).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_ShouldReturnUnauthorized() throws Exception {
        when(request.getHeader(("Authorization"))).thenReturn(INVALID_TOKEN);
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1))
                .sendError(HttpServletResponse.SC_UNAUTHORIZED, InfrastructureConstants.INVALID_TOKEN);
    }

    @Test
    void doFilterInternal_ExpiredToken_ShouldReturnUnauthorized() throws Exception {
        when(request.getHeader(("Authorization"))).thenReturn(EXPIRED_TOKEN);
        when(jwtService.isTokenValid(anyString())).thenThrow(new ExpiredJwtException(null, null, "Token Expired"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1))
                .sendError(HttpServletResponse.SC_UNAUTHORIZED, InfrastructureConstants.TOKEN_EXPIRED);
    }

    @Test
    void doFilterInternal_ValidToken_ShouldAuthenticateUser() throws Exception {

        String userId = "1";

        UserEntity user = new UserEntity();
        user.setId(1L);

        RoleEntity role = new RoleEntity();
        role.setName(RoleName.CUSTOMER);
        user.setRole(role);

        when(request.getHeader(("Authorization"))).thenReturn(VALID_TOKEN);
        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn(userId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).isTokenValid(anyString());
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(userRepository, times(1)).findById(anyLong());
    }
}