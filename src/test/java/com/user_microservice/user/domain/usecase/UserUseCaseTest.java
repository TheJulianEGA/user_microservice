package com.user_microservice.user.domain.usecase;

import com.user_microservice.user.domain.exception.DocumentNumberAlreadyExistsException;
import com.user_microservice.user.domain.exception.EmailAlreadyExistsException;
import com.user_microservice.user.domain.exception.UserNotOfLegalAgeException;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.spi.IUserPersistencePort;
import com.user_microservice.user.domain.util.DomainConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userModelPersistencePort;

    @InjectMocks
    private UserUseCase userModelUseCase;

    private User userModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userModel = new User();
        userModel.setDocumentNumber("123");
        userModel.setBirthDate(LocalDate.of(2000, 1, 1));
        userModel.setEmail("user@example.co");

    }

    @Test
    @DisplayName("Given valid user data, when registering user, then return registered user")
    void givenValidUserData_whenRegisteringUser_thenReturnRegisteredUser() {

        when(userModelPersistencePort.registerUser(userModel)).thenReturn(userModel);

        User result = userModelUseCase.registerUser(userModel);

        assertNotNull(result);
        assertEquals(userModel, result);

        verify(userModelPersistencePort, times(1)).registerUser(userModel);
    }

    @Test
    @DisplayName("Given underage user, when registering user, then throw UserNotOfLegalAgeException")
    void givenUnderageUser_whenRegisteringUser_thenThrowUserNotOfLegalAgeException() {

        userModel.setBirthDate(LocalDate.now());

        UserNotOfLegalAgeException exception = assertThrows(UserNotOfLegalAgeException.class,
                () -> userModelUseCase.registerUser(userModel));

        assertEquals(DomainConstants.USER_NOT_OF_LEGAL_EGE, exception.getMessage());
        verify(userModelPersistencePort, never()).existsUserByEmail(any());
    }

    @Test
    @DisplayName("Given existing email, when registering user, then throw EmailAlreadyExistsException")
    void givenExistingEmail_whenRegisteringUser_thenThrowEmailAlreadyExistsException() {

        when(userModelPersistencePort.existsUserByEmail(userModel.getEmail())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userModelUseCase.registerUser(userModel));

        assertEquals(DomainConstants.USER_EMAIL_ALREADY_EXISTS, exception.getMessage());
        verify(userModelPersistencePort, never()).registerUser(any());
    }

    @Test
    @DisplayName("Given existing identification, when registering user, then throw IdentificationAlreadyExistsException")
    void givenExistingIdentification_whenRegisteringUser_thenThrowIdentificationAlreadyExistsException() {

        when(userModelPersistencePort.existsUserByDocumentNumber(userModel.getDocumentNumber())).thenReturn(true);

        DocumentNumberAlreadyExistsException exception = assertThrows(DocumentNumberAlreadyExistsException.class,
                () -> userModelUseCase.registerUser(userModel));

        assertEquals(DomainConstants.USER_DOCUMENT_NUMBER_ALREADY_EXISTS, exception.getMessage());
        verify(userModelPersistencePort, never()).registerUser(any());
    }
}
