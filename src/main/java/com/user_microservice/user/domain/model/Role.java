package com.user_microservice.user.domain.model;

import com.user_microservice.user.domain.util.RoleName;


public class Role {

    private Long id;

    private RoleName name;

    private String description;

    public Role(Long id, RoleName name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Role() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
