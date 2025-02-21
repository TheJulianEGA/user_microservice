package com.user_microservice.user.application.handler.userhandler;

import com.user_microservice.user.application.dto.userdto.UserRequest;
import com.user_microservice.user.application.dto.userdto.UserResponse;
import com.user_microservice.user.application.mapper.usermapper.IUserRequestMapper;
import com.user_microservice.user.application.mapper.usermapper.IUserResponseMapper;
import com.user_microservice.user.domain.api.IRoleServicePort;
import com.user_microservice.user.domain.api.IUserServicePort;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.util.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHandler implements IUserHandler {

    private final IRoleServicePort roleServicePort;
    private final IUserServicePort userServicePort;
    private final IUserRequestMapper userRequestMapper;
    private final IUserResponseMapper userResponseMapper;

    @Override
    public UserResponse registerUser(UserRequest userRequest) {

        roleServicePort.existsRoleByName(userRequest.getRole().toUpperCase());

        String roleNameString = userRequest.getRole().toUpperCase();
        RoleName roleName = RoleName.valueOf(roleNameString);

        Role role = roleServicePort.getRoleByName(roleName);

        User user = userRequestMapper.toModel(userRequest);
        user.setRole(role);

        User registeredUser = userServicePort.registerUser(user);

        return userResponseMapper.toResponse(registeredUser);
    }

    @Override
    public boolean existsUserWithOwnerRole(Long userId) {

        return userServicePort.existsUserWithOwnerRole(userId);
    }
}
