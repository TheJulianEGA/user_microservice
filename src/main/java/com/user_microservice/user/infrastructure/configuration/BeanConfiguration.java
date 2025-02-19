package com.user_microservice.user.infrastructure.configuration;

import com.user_microservice.user.domain.api.IRoleServicePort;
import com.user_microservice.user.domain.api.IUserServicePort;
import com.user_microservice.user.domain.spi.IRolePersistencePort;
import com.user_microservice.user.domain.spi.IUserPersistencePort;
import com.user_microservice.user.domain.usecase.RoleUseCase;
import com.user_microservice.user.domain.usecase.UserUseCase;
import com.user_microservice.user.infrastructure.persistence.jpa.adapter.RoleJpaAdapter;
import com.user_microservice.user.infrastructure.persistence.jpa.adapter.UserJpaAdapter;
import com.user_microservice.user.infrastructure.persistence.jpa.mapper.IRoleEntityMapper;
import com.user_microservice.user.infrastructure.persistence.jpa.mapper.IUserEntityMapper;
import com.user_microservice.user.infrastructure.persistence.jpa.repository.IRoleRepository;
import com.user_microservice.user.infrastructure.persistence.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;
    private final IRoleRepository roleRepository;
    private final IRoleEntityMapper roleEntityMapper;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public IRolePersistencePort rolePersistencePort() {
        return new RoleJpaAdapter(roleRepository,roleEntityMapper);
    }

    @Bean
    public IUserPersistencePort userModelPersistencePort() {
        return new UserJpaAdapter(userRepository,userEntityMapper,passwordEncoder);
    }

    @Bean
    public IUserServicePort userModelServicePort(IUserPersistencePort userModelPersistencePort) {
        return new UserUseCase(userModelPersistencePort);
    }

    @Bean
    public IRoleServicePort roleModelServicePort(IRolePersistencePort roleModelPersistencePort) {
        return new RoleUseCase(roleModelPersistencePort);
    }

}
