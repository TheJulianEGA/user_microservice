package com.user_microservice.user.domain.usecase;

import com.user_microservice.user.domain.api.IRoleServicePort;
import com.user_microservice.user.domain.exception.RoleNameNotFoundException;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.spi.IRolePersistencePort;
import com.user_microservice.user.domain.util.DomainConstants;
import com.user_microservice.user.domain.util.RoleName;

import java.util.Arrays;

public class RoleUseCase implements IRoleServicePort {

    private final IRolePersistencePort rolePersistencePort;

    public RoleUseCase(IRolePersistencePort rolePersistencePort) {
        this.rolePersistencePort = rolePersistencePort;
    }

    @Override
    public Role getRoleByName(RoleName name) {
        return rolePersistencePort.getRoleByName(name);
    }

    @Override
    public boolean existsRoleByName(String name) {

        boolean isValidRole = Arrays.stream(RoleName.values())
                .anyMatch(role -> role.name().equalsIgnoreCase(name));

        if (!isValidRole) {
            throw new RoleNameNotFoundException(DomainConstants.ROLE_NAME_NOT_FOUND);
        }

        return true;
    }
}

