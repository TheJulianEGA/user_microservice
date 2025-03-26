package com.user_microservice.user.domain.security;

import com.user_microservice.user.domain.model.User;

public interface IAuthenticationSecurityPort {

    User authenticate(String email, String password);
    String generateToken(User user);
    boolean validateCredentials(String userEmail, String userPassword);
    Long getAuthenticatedUserId();

}
