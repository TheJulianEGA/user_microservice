package com.user_microservice.user.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    private String name;

    private String lastName;

    private String documentNumber;

    private String phone;

    private LocalDate birthDate;

    private String email;

    private String password;

    private Role role;

}
