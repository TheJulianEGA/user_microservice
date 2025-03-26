package com.user_microservice.user.domain.spi;

import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.util.RoleName;

public interface IRolePersistencePort {

    Role getRoleByName(RoleName name);
}
