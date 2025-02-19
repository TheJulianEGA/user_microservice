package com.user_microservice.user.infrastructure.persistence.jpa.adapter;

import com.user_microservice.user.domain.exception.RoleNameNotFoundException;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.util.RoleName;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.RoleEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.mapper.IRoleEntityMapper;
import com.user_microservice.user.infrastructure.persistence.jpa.repository.IRoleRepository;
import com.user_microservice.user.infrastructure.util.InfrastructureConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleJpaAdapterTest {

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IRoleEntityMapper roleEntityMapper;

    @InjectMocks
    private RoleJpaAdapter roleJpaAdapter;

    private RoleName roleName;
    private RoleEntity roleEntity;
    private Role roleModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        roleName = RoleName.ADMINISTRATOR;
        roleEntity = new RoleEntity();
        roleModel = new Role();
    }

    @Test
    @DisplayName("Given existing role name, when getRoleByName, then return RoleModel")
    void givenExistingRoleName_whenGetRoleByName_thenReturnRoleModel() {

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));
        when(roleEntityMapper.toModel(roleEntity)).thenReturn(roleModel);

        Role result = roleJpaAdapter.getRoleByName(roleName);

        assertNotNull(result);
        assertEquals(roleModel, result);
        verify(roleRepository, times(1)).findByName(roleName);
        verify(roleEntityMapper, times(1)).toModel(roleEntity);
    }

    @Test
    @DisplayName("Given non-existing role name, when getRoleByName, then throw RoleNameNotFoundException")
    void givenNonExistingRoleName_whenGetRoleByName_thenThrowRoleNameNotFoundException() {

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        RoleNameNotFoundException exception = assertThrows(RoleNameNotFoundException.class,
                () -> roleJpaAdapter.getRoleByName(roleName));

        Assertions.assertEquals(InfrastructureConstants.ROLE_NOT_FUND, exception.getMessage());

        verify(roleRepository, times(1)).findByName(roleName);
        verify(roleEntityMapper, never()).toModel(any());
    }
}