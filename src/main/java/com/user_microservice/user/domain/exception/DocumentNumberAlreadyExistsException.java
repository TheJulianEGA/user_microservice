package com.user_microservice.user.domain.exception;

public class DocumentNumberAlreadyExistsException extends RuntimeException {
    public DocumentNumberAlreadyExistsException(String message) {
        super(message);
    }
}