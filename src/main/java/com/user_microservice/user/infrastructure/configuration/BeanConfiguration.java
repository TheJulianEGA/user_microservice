package com.user_microservice.user.infrastructure.configuration;

import com.user_microservice.user.domain.api.IAuthenticationServicePort;
import com.user_microservice.user.domain.api.IRoleServicePort;
import com.user_microservice.user.domain.api.IUserServicePort;
import com.user_microservice.user.domain.security.IAuthenticationSecurityPort;
import com.user_microservice.user.domain.spi.IRolePersistencePort;
import com.user_microservice.user.domain.spi.IUserPersistencePort;
import com.user_microservice.user.domain.usecase.AuthenticationUseCase;
import com.user_microservice.user.domain.usecase.RoleUseCase;
import com.user_microservice.user.domain.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    @Bean
    public IUserServicePort userModelServicePort(
            IUserPersistencePort userModelPersistencePort,
            IAuthenticationSecurityPort authenticationSecurityPort
    ) {
        return new UserUseCase(userModelPersistencePort, authenticationSecurityPort);
    }

    @Bean
    public IRoleServicePort roleModelServicePort(IRolePersistencePort roleModelPersistencePort) {
        return new RoleUseCase(roleModelPersistencePort);
    }

    @Bean
    public IAuthenticationServicePort authenticationServicePort(
            IAuthenticationSecurityPort authenticationSecurityPort)
    {
        return new AuthenticationUseCase(authenticationSecurityPort);
    }

}
