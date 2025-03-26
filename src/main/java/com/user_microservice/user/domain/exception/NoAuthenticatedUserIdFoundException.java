package com.user_microservice.user.domain.exception;

public class NoAuthenticatedUserIdFoundException extends RuntimeException {
    public NoAuthenticatedUserIdFoundException(String message) {
        super(message);
    }
}
