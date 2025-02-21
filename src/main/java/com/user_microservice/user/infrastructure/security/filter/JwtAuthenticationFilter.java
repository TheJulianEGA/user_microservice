package com.user_microservice.user.infrastructure.security.filter;

import com.user_microservice.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.repository.IUserRepository;
import com.user_microservice.user.infrastructure.security.service.IJwtService;
import com.user_microservice.user.infrastructure.util.InfrastructureConstants;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final IUserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {

        String authHeader = request.getHeader(InfrastructureConstants.AUTH_HEADER);

        if (isInvalidAuthHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = extractJwtFromHeader(authHeader);
        if (!processJwtAuthentication(jwt, response)) {
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isInvalidAuthHeader(String authHeader) {

        return authHeader == null || !authHeader.startsWith(InfrastructureConstants.TOKEN_PREFIX);
    }

    private String extractJwtFromHeader(String authHeader) {
        return authHeader.substring(InfrastructureConstants.TOKEN_PREFIX_LENGTH);
    }

    private boolean processJwtAuthentication(String jwt, HttpServletResponse response) throws IOException {
        try {
            if (!jwtService.isTokenValid(jwt)) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        InfrastructureConstants.INVALID_TOKEN
                );
                return false;
            }

            String userName = jwtService.extractUsername(jwt);
            authenticateUser(userName);

        } catch (ExpiredJwtException e) {
            handleExpiredToken(response,e);
            return false;
        } catch (Exception e) {
            handleInvalidToken(response,e);
            return false;
        }

        return true;
    }

    private void authenticateUser(String userName) {
        UserEntity user = userRepository.findById(Long.parseLong(userName))
                .orElseThrow(() -> new RuntimeException(InfrastructureConstants.USER_NOT_FOUND));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userName, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void handleExpiredToken(HttpServletResponse response, ExpiredJwtException e) throws IOException {
        LOGGER.warn(e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, InfrastructureConstants.TOKEN_EXPIRED);
    }

    private void handleInvalidToken(HttpServletResponse response, Exception e) throws IOException {
        LOGGER.warn(e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, InfrastructureConstants.INVALID_TOKEN);
    }
}