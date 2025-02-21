package com.user_microservice.user.application.handler;

import com.user_microservice.user.application.dto.userdto.UserRequest;
import com.user_microservice.user.application.dto.userdto.UserResponse;
import com.user_microservice.user.application.handler.userhandler.UserHandler;
import com.user_microservice.user.application.mapper.usermapper.IUserRequestMapper;
import com.user_microservice.user.application.mapper.usermapper.IUserResponseMapper;
import com.user_microservice.user.domain.api.IRoleServicePort;
import com.user_microservice.user.domain.api.IUserServicePort;
import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.util.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserHandlerTest {

    @Mock
    private IRoleServicePort roleModelServicePort;

    @Mock
    private IUserServicePort userModelServicePort;

    @Mock
    private IUserRequestMapper userRequestMapper;

    @Mock
    private IUserResponseMapper userResponseMapper;


    @InjectMocks
    private UserHandler userHandler;

    private UserRequest userRequest;
    private Role roleModel;
    private User userModel;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRequest = new UserRequest();
        userRequest.setRole("ADMINISTRATOR");

        roleModel = new Role();

        userModel = new User();
        userModel.setEmail("julian@mail.com");
        userModel.setBirthDate(LocalDate.of(2000, 1, 1));
        userModel.setDocumentNumber("123456789");

        userResponse = new UserResponse();
    }


    @Test
    @DisplayName("Given valid user data, when registering user, then return registered user")
    void givenValidUserData_whenRegisteringUser_thenReturnRegisteredUser() {

        when(roleModelServicePort.getRoleByName(RoleName.ADMINISTRATOR)).thenReturn(roleModel);
        when(userRequestMapper.toModel(userRequest)).thenReturn(userModel);
        when(userModelServicePort.registerUser(userModel)).thenReturn(userModel);
        when(userResponseMapper.toResponse(userModel)).thenReturn(userResponse);

        UserResponse result = userHandler.registerUser(userRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(roleModelServicePort, times(1)).existsRoleByName("ADMINISTRATOR");
        verify(roleModelServicePort, times(1)).getRoleByName(RoleName.ADMINISTRATOR);
        verify(userRequestMapper, times(1)).toModel(userRequest);
        verify(userModelServicePort, times(1)).registerUser(userModel);
        verify(userResponseMapper, times(1)).toResponse(userModel);
    }
}