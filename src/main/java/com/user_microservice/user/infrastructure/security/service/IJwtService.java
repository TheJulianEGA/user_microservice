package com.user_microservice.user.infrastructure.security.service;

import com.user_microservice.user.domain.model.User;

import java.util.Map;

public interface IJwtService {

    String generateToken(User user, Map<String, Object> extraClaims);
    String extractUsername(String jwt);
    boolean isTokenValid(String token);

}
