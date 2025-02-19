package com.user_microservice.user.domain.usecase;

import com.user_microservice.user.domain.api.IUserServicePort;
import com.user_microservice.user.domain.exception.DocumentNumberAlreadyExistsException;
import com.user_microservice.user.domain.exception.EmailAlreadyExistsException;
import com.user_microservice.user.domain.exception.UserNotOfLegalAgeException;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.spi.IUserPersistencePort;
import com.user_microservice.user.domain.util.DomainConstants;

import java.time.LocalDate;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;

    public UserUseCase(IUserPersistencePort persistencePort) {
        this.userPersistencePort = persistencePort;
    }

    @Override
    public User registerUser(User user) {

        validateUser( user);

        return userPersistencePort.registerUser(user);
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
