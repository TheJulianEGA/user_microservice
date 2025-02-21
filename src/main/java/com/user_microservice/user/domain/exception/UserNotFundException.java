package com.user_microservice.user.domain.exception;

public class UserNotFundException extends RuntimeException{
    public UserNotFundException(String message) {
        super(message);
    }
}
