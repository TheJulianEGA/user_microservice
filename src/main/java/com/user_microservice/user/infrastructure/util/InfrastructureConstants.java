package com.user_microservice.user.infrastructure.util;

public class InfrastructureConstants {

    public static final String ROLE_NOT_FUND = "Role not found";
    public static final String USER_NOT_FOUND = "The user was not found";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final long TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 10;
    public static final String INVALID_USER_CREDENTIALS = "User with invalid credentials";
    public static final String AUTHORITIES_KEY = "authorities";
    public static final String EXTRA_CLAIMS_ERROR = "Error generating extra claims";
    public static final String AUTH_HEADER = "Authorization";
    public static final int TOKEN_PREFIX_LENGTH = 7;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String TOKEN_EXPIRED = "The token has expired";
    public static final String ACCOUNT_BLOCKED = "Account blocked. Try again in 30 seconds.";
    public static final String ROLE_ADMINISTRATOR = "hasRole('ADMINISTRATOR')";
    public static final String ROLE_OWNER = "hasRole('OWNER')";
    public static final String ROLE_EMPLOYEE = "hasRole('EMPLOYEE')";
    public static final String ROLE_CUSTOMER = "hasRole('CUSTOMER')";
    public static final String NO_AUTHENTICATED_USER_ID_FOUND = "No authenticated user id found";

    private InfrastructureConstants() {}
}
