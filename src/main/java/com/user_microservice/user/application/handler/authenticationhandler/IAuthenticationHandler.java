package com.user_microservice.user.application.handler.authenticationhandler;

import com.user_microservice.user.application.dto.authenticationdto.AuthenticationRequest;
import com.user_microservice.user.application.dto.authenticationdto.AuthenticationResponse;

public interface IAuthenticationHandler {

    AuthenticationResponse login (AuthenticationRequest request);

}
