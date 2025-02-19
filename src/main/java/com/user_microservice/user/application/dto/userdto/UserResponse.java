package com.user_microservice.user.application.dto.userdto;

import com.user_microservice.user.application.dto.roledto.RoleResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UserResponse {

    private Long id;

    private String name;

    private String lastName;

    private String documentNumber;

    private String phone;

    private LocalDate birthDate;

    private String email;

    private RoleResponse role;
}
