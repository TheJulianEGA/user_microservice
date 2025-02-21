package com.user_microservice.user.domain.util;

public class DomainConstants {

    public static final String USER_NOT_OF_LEGAL_EGE = "The user must be of legal age";
    public static final String USER_DOCUMENT_NUMBER_ALREADY_EXISTS = "The Document Number already exists";
    public static final String USER_EMAIL_ALREADY_EXISTS = "The email already exists";
    public static final String ROLE_NAME_NOT_FOUND = "The role name was not found" ;
    public static final String USER_NOT_FOUND = "The user was not found";
    public static final String INVALID_USER_CREDENTIALS = "User with invalid credentials";
    public static final String ONLY_ADMIN_CAN_CREATE_OWNER = "Only an administrator can create an owner.";
    public static final String ONLY_OWNER_CAN_CREATE_EMPLOYEE = "Only an owner can create an employee.";
    public static final String NOT_ALLOWED_TO_CREATE_CUSTOMER_OR_ADMIN = "Creating users with the role CUSTOMER or ADMINISTRATOR is not allowed.";

    private DomainConstants(){}
}
