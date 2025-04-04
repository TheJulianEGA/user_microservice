package com.user_microservice.user.infrastructure.persistence.jpa.repository;

import com.user_microservice.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByDocumentNumber(String documentNumber);
    Optional<UserEntity> findByEmail(String email);


}
