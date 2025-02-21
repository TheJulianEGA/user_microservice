package com.user_microservice.user.domain.usecase;

import com.user_microservice.user.domain.api.IUserServicePort;
import com.user_microservice.user.domain.exception.*;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.security.IAuthenticationSecurityPort;
import com.user_microservice.user.domain.spi.IUserPersistencePort;
import com.user_microservice.user.domain.util.DomainConstants;
import com.user_microservice.user.domain.util.RoleName;

import java.time.LocalDate;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IAuthenticationSecurityPort authenticationSecurityPort;


    public UserUseCase(IUserPersistencePort persistencePort, IAuthenticationSecurityPort authenticationSecurityPort) {
        this.userPersistencePort = persistencePort;
        this.authenticationSecurityPort = authenticationSecurityPort;
    }

    public User registerUser(User user) {
        Long authenticatedUserId = authenticationSecurityPort.getAuthenticatedUserId();
        validatePermissions(authenticatedUserId, user.getRole().getName());

        validateUser(user);

        return userPersistencePort.registerUser(user);
    }

    private void validatePermissions(Long authenticatedUserId, RoleName newUserRole) {
        User authenticatedUser = userPersistencePort.getUserById(authenticatedUserId);

        if (newUserRole == RoleName.OWNER && authenticatedUser.getRole().getName() != RoleName.ADMINISTRATOR) {
            throw new InvalidUserException(DomainConstants.ONLY_ADMIN_CAN_CREATE_OWNER);
        }

        if (newUserRole == RoleName.EMPLOYEE && authenticatedUser.getRole().getName() != RoleName.OWNER) {
            throw new InvalidUserException(DomainConstants.ONLY_OWNER_CAN_CREATE_EMPLOYEE);
        }

        if (newUserRole == RoleName.CUSTOMER || newUserRole == RoleName.ADMINISTRATOR) {
            throw new InvalidUserException(DomainConstants.NOT_ALLOWED_TO_CREATE_CUSTOMER_OR_ADMIN);
        }
    }

    @Override
    public boolean existsUserWithOwnerRole(Long userId) {

        User user = userPersistencePort.getUserById(userId);

        if (user == null) {
            throw new UserNotFundException(DomainConstants.USER_NOT_FOUND);
        }

        return RoleName.OWNER.equals(user.getRole().getName());
    }

    private void validateUser(User user) {

        if (LocalDate.now().minusYears(18).isBefore(user.getBirthDate())) {
            throw new UserNotOfLegalAgeException(DomainConstants.USER_NOT_OF_LEGAL_EGE);
        }
        if (userPersistencePort.existsUserByDocumentNumber(user.getDocumentNumber())) {
            throw new DocumentNumberAlreadyExistsException(DomainConstants.USER_DOCUMENT_NUMBER_ALREADY_EXISTS);
        }
        if (userPersistencePort.existsUserByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(DomainConstants.USER_EMAIL_ALREADY_EXISTS);
        }
    }
}
