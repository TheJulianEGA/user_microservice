package com.user_microservice.user.infrastructure.security.adapter;

import com.user_microservice.user.domain.exception.ExtraClaimsException;
import com.user_microservice.user.domain.exception.InvalidCredentialsException;
import com.user_microservice.user.domain.exception.NoAuthenticatedUserIdFoundException;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.security.IAuthenticationSecurityPort;
import com.user_microservice.user.domain.spi.IRolePersistencePort;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.mapper.IUserEntityMapper;
import com.user_microservice.user.infrastructure.security.service.JwtService;
import com.user_microservice.user.infrastructure.util.InfrastructureConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class AuthenticationSecurityAdapter implements IAuthenticationSecurityPort {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IRolePersistencePort rolePersistencePort;
    private final IUserEntityMapper userEntityMapper;

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME = 30000;

    private final ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockoutTimestamps = new ConcurrentHashMap<>();

    @Override
    public User authenticate(String email, String password) {
        checkLockout(email);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            UserEntity userEntity = (UserEntity) authentication.getPrincipal();

            failedAttempts.remove(email);
            lockoutTimestamps.remove(email);

            return userEntityMapper.toModel(userEntity);

        } catch (BadCredentialsException e) {
            registerFailedAttempt(email);
            throw new InvalidCredentialsException(InfrastructureConstants.INVALID_USER_CREDENTIALS, e);
        }
    }

    @Override
    public String generateToken(User user) {
        return jwtService.generateToken(user, generateExtraClaims(user));
    }

    @Override
    public boolean validateCredentials(String userEmail, String userPassword) {
        try {
            checkLockout(userEmail);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userEmail, userPassword));

            failedAttempts.remove(userEmail);
            lockoutTimestamps.remove(userEmail);

            return authentication.isAuthenticated();

        } catch (BadCredentialsException e) {
            registerFailedAttempt(userEmail);
            throw new InvalidCredentialsException(InfrastructureConstants.INVALID_USER_CREDENTIALS, e);
        }
    }

        @Override
        public Long getAuthenticatedUserId() {

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                return Long.valueOf(userDetails.getUsername());
            } else if (principal instanceof String userId) {
                return Long.valueOf(userId);
            } else {
                throw new NoAuthenticatedUserIdFoundException(InfrastructureConstants.NO_AUTHENTICATED_USER_ID_FOUND);
            }
        }


    private void checkLockout(String email) {
        if (lockoutTimestamps.containsKey(email)) {
            long lockTime = lockoutTimestamps.get(email);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lockTime < LOCK_TIME) {
                throw new InvalidCredentialsException(InfrastructureConstants.ACCOUNT_BLOCKED);
            } else {
                lockoutTimestamps.remove(email);
                failedAttempts.remove(email);
            }
        }
    }

    private void registerFailedAttempt(String email) {
        int attempts = failedAttempts.getOrDefault(email, 0) + 1;
        failedAttempts.put(email, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            lockoutTimestamps.put(email, System.currentTimeMillis());
        }
    }

    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        try {
            extraClaims.put("email", user.getEmail());
            Role role = rolePersistencePort.getRoleByName(user.getRole().getName());
            extraClaims.put(
                    InfrastructureConstants.AUTHORITIES_KEY,
                    InfrastructureConstants.ROLE_PREFIX + role.getName().name());

        } catch (Exception e) {
            throw new ExtraClaimsException(InfrastructureConstants.EXTRA_CLAIMS_ERROR, e);
        }
        return extraClaims;
    }
}