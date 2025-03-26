package com.user_microservice.user.domain.usecase;

import com.user_microservice.user.domain.api.IAuthenticationServicePort;
import com.user_microservice.user.domain.exception.AuthenticationException;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.security.IAuthenticationSecurityPort;
import com.user_microservice.user.domain.util.DomainConstants;


public class AuthenticationUseCase implements IAuthenticationServicePort {

    private final IAuthenticationSecurityPort authenticationSecurityPort;

    public AuthenticationUseCase(IAuthenticationSecurityPort authenticationSecurityPort) {
        this.authenticationSecurityPort = authenticationSecurityPort;
    }

    @Override
    public String login(String email, String password) {

        if (!authenticationSecurityPort.validateCredentials(email, password)) {
            throw new AuthenticationException(DomainConstants.INVALID_USER_CREDENTIALS);
        }

        User user = authenticationSecurityPort.authenticate(email, password);

        return authenticationSecurityPort.generateToken(user);
    }
}
