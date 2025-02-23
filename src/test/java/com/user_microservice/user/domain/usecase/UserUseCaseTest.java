package com.user_microservice.user.domain.usecase;

import com.user_microservice.user.domain.exception.*;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.security.IAuthenticationSecurityPort;
import com.user_microservice.user.domain.spi.IUserPersistencePort;
import com.user_microservice.user.domain.util.DomainConstants;
import com.user_microservice.user.domain.util.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IAuthenticationSecurityPort authenticationSecurityPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private User newUser;
    private User authenticatedUser;
    private Long authenticatedUserId;

    @BeforeEach
    void setUp() {
        authenticatedUserId = 1L;

        authenticatedUser = new User();
        authenticatedUser.setRole(new Role());

        newUser = new User();
        newUser.setBirthDate(LocalDate.now().minusYears(20));
        newUser.setEmail("user@example.com");
        newUser.setDocumentNumber("12345");
        newUser.setRole(new Role());
    }

    @Test
    void testRegisterUser_Success() {
        authenticatedUser.getRole().setName(RoleName.ADMINISTRATOR);
        newUser.getRole().setName(RoleName.OWNER);

        when(authenticationSecurityPort.getAuthenticatedUserId()).thenReturn(authenticatedUserId);
        when(userPersistencePort.getUserById(authenticatedUserId)).thenReturn(authenticatedUser);
        when(userPersistencePort.existsUserByEmail(newUser.getEmail())).thenReturn(false);
        when(userPersistencePort.existsUserByDocumentNumber(newUser.getDocumentNumber())).thenReturn(false);
        when(userPersistencePort.registerUser(newUser)).thenReturn(newUser);

        User result = userUseCase.registerUser(newUser);
        assertNotNull(result);

        verify(authenticationSecurityPort, times(1)).getAuthenticatedUserId();
        verify(userPersistencePort, times(1)).getUserById(authenticatedUserId);
        verify(userPersistencePort, times(1)).existsUserByEmail(newUser.getEmail());
        verify(userPersistencePort, times(1)).existsUserByDocumentNumber(newUser.getDocumentNumber());
        verify(userPersistencePort, times(1)).registerUser(newUser);
    }

    @Test
    void testRegisterUser_NotOfLegalAge() {
        newUser.setBirthDate(LocalDate.now().minusYears(17));

        UserNotOfLegalAgeException exception = assertThrows(UserNotOfLegalAgeException.class,
                () -> userUseCase.registerUser(newUser));
        assertEquals(DomainConstants.USER_NOT_OF_LEGAL_EGE, exception.getMessage());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userPersistencePort.existsUserByEmail(newUser.getEmail())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userUseCase.registerUser(newUser));

        assertEquals(DomainConstants.USER_EMAIL_ALREADY_EXISTS, exception.getMessage());

        verify(userPersistencePort, times(1)).existsUserByEmail(newUser.getEmail());
    }

    @Test
    void testRegisterUser_DocumentNumberAlreadyExists() {
        when(userPersistencePort.existsUserByDocumentNumber(newUser.getDocumentNumber())).thenReturn(true);

        DocumentNumberAlreadyExistsException exception = assertThrows(DocumentNumberAlreadyExistsException.class,
                () -> userUseCase.registerUser(newUser));
        assertEquals(DomainConstants.USER_DOCUMENT_NUMBER_ALREADY_EXISTS, exception.getMessage());

        verify(userPersistencePort, times(1)).existsUserByDocumentNumber(newUser.getDocumentNumber());
    }

    @Test
    void testRegisterUser_ForbiddenRoleAssignments() {
        authenticatedUser.getRole().setName(RoleName.OWNER);

        User newCustomer = new User();
        newCustomer.setBirthDate(LocalDate.now().minusYears(20));
        newCustomer.setEmail("customer@example.com");
        newCustomer.setDocumentNumber("77777");
        newCustomer.setRole(new Role());
        newCustomer.getRole().setName(RoleName.CUSTOMER);

        User newAdmin = new User();
        newAdmin.setBirthDate(LocalDate.now().minusYears(20));
        newAdmin.setEmail("admin@example.com");
        newAdmin.setDocumentNumber("88888");
        newAdmin.setRole(new Role());
        newAdmin.getRole().setName(RoleName.ADMINISTRATOR);

        when(authenticationSecurityPort.getAuthenticatedUserId()).thenReturn(authenticatedUserId);
        when(userPersistencePort.getUserById(authenticatedUserId)).thenReturn(authenticatedUser);
        when(userPersistencePort.existsUserByEmail(anyString())).thenReturn(false);

        assertThrows(InvalidUserException.class, () -> userUseCase.registerUser(newCustomer));
        assertThrows(InvalidUserException.class, () -> userUseCase.registerUser(newAdmin));

        verify(authenticationSecurityPort, times(2)).getAuthenticatedUserId();
        verify(userPersistencePort, times(2)).getUserById(authenticatedUserId);
        verify(userPersistencePort, times(2)).existsUserByEmail(anyString());
    }

    @Test
    void testExistsUserWithOwnerRole_UserIsOwner() {
        User user = new User();
        user.setRole(new Role());
        user.getRole().setName(RoleName.OWNER);

        when(userPersistencePort.getUserById(authenticatedUserId)).thenReturn(user);

        boolean result = userUseCase.existsUserWithOwnerRole(authenticatedUserId);
        assertTrue(result);

        verify(userPersistencePort, times(1)).getUserById(authenticatedUserId);
    }

    @Test
    void testExistsUserWithOwnerRole_UserIsNotOwner() {
        User user = new User();
        user.setRole(new Role());
        user.getRole().setName(RoleName.EMPLOYEE);

        when(userPersistencePort.getUserById(authenticatedUserId)).thenReturn(user);

        boolean result = userUseCase.existsUserWithOwnerRole(authenticatedUserId);
        assertFalse(result);

        verify(userPersistencePort, times(1)).getUserById(authenticatedUserId);
    }

    @Test
    void testExistsUserWithOwnerRole_UserNotFound() {
        when(userPersistencePort.getUserById(authenticatedUserId)).thenReturn(null);

        UserNotFundException exception = assertThrows(UserNotFundException.class, () -> userUseCase.existsUserWithOwnerRole(authenticatedUserId));
        assertEquals(DomainConstants.USER_NOT_FOUND, exception.getMessage());

        verify(userPersistencePort, times(1)).getUserById(authenticatedUserId);
    }
}

