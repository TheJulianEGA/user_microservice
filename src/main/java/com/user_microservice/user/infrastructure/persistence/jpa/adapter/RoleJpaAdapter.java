package com.user_microservice.user.infrastructure.persistence.jpa.adapter;

import com.user_microservice.user.domain.exception.RoleNameNotFoundException;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.spi.IRolePersistencePort;
import com.user_microservice.user.domain.util.RoleName;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.RoleEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.mapper.IRoleEntityMapper;
import com.user_microservice.user.infrastructure.persistence.jpa.repository.IRoleRepository;
import com.user_microservice.user.infrastructure.util.InfrastructureConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleJpaAdapter implements IRolePersistencePort {

    private final IRoleRepository roleRepository;
    private final IRoleEntityMapper roleEntityMapper;

    @Override
    public Role getRoleByName(RoleName name) {

        RoleEntity roleEntity = roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNameNotFoundException(InfrastructureConstants.ROLE_NOT_FUND));

        return roleEntityMapper.toModel(roleEntity);
    }
}
