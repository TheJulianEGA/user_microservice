package com.user_microservice.user.infrastructure.persistence.jpa.mapper;

import com.user_microservice.user.domain.model.Role;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRoleEntityMapper {

    Role toModel(RoleEntity roleEntity);
}
