package com.user_microservice.user.domain.api;

import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.util.RoleName;

public interface IRoleServicePort {

    Role getRoleByName(RoleName name);

    boolean existsRoleByName(String name);

}
