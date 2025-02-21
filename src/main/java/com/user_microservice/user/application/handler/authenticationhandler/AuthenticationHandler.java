package com.user_microservice.user.application.handler.authenticationhandler;

import com.user_microservice.user.application.dto.authenticationdto.AuthenticationRequest;
import com.user_microservice.user.application.dto.authenticationdto.AuthenticationResponse;
import com.user_microservice.user.domain.api.IAuthenticationServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationHandler implements IAuthenticationHandler {

    private final IAuthenticationServicePort authenticationServicePort;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {

        String token = authenticationServicePort.login(request.getEmail(), request.getPassword());

        return new AuthenticationResponse(token);
    }
}
