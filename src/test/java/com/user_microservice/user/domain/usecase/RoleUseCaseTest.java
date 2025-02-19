package com.user_microservice.user.domain.usecase;

import com.user_microservice.user.domain.exception.RoleNameNotFoundException;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.spi.IRolePersistencePort;
import com.user_microservice.user.domain.util.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleUseCaseTest {

    @Mock
    private IRolePersistencePort roleModelPersistencePort;

    @InjectMocks
    private RoleUseCase roleModelUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return role when valid name is provided")
    void givenValidRoleName_whenGetRoleByName_thenReturnRole() {

        RoleName roleName = RoleName.ADMINISTRATOR;
        Role expectedRole = new Role();

        when(roleModelPersistencePort.getRoleByName(roleName)).thenReturn(expectedRole);

        Role result = roleModelUseCase.getRoleByName(roleName);

        assertEquals(expectedRole, result);
        verify(roleModelPersistencePort, times(1)).getRoleByName(roleName);
    }

    @Test
    @DisplayName("Should return true when role name exists")
    void givenValidRoleName_whenCheckingIfRoleExists_thenReturnTrue() {

        String validRole = "ADMINISTRATOR";

        boolean result = roleModelUseCase.existsRoleByName(validRole);

        assertTrue(result);
    }

    @Test
    @DisplayName("Given invalid role name, when checking if role exists, then throw RoleNameNotFoundException")
    void givenInvalidRoleName_whenCheckingIfRoleExists_thenThrowRoleNameNotFoundException() {
        String roleName = "INVALID_ROLE";

        RoleNameNotFoundException exception = assertThrows(RoleNameNotFoundException.class,
                () -> roleModelUseCase.existsRoleByName(roleName));
        verify(roleModelPersistencePort, never()).getRoleByName(any());

        assertEquals("The role name was not found", exception.getMessage());
    }
}
