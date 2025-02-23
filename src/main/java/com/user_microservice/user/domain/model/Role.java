package com.user_microservice.user.domain.model;

import com.user_microservice.user.domain.util.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    private Long id;

    private RoleName name;

    private String description;

}
