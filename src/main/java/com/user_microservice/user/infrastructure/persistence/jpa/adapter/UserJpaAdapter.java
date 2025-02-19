package com.user_microservice.user.infrastructure.persistence.jpa.adapter;

import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.domain.spi.IUserPersistencePort;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.mapper.IUserEntityMapper;
import com.user_microservice.user.infrastructure.persistence.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;
    //private final PasswordEncoder passwordEncoder;


    @Override
    public User resgisterUser(User user) {

        /*
        String encoderPassword = passwordEncoder.encode(userModel.getPassword());
        userModel.setPassword(encoderPassword);
        */

        UserEntity userEntity = userEntityMapper.toEntity(user);
        userRepository.save(userEntity);

        return userEntityMapper.toModel(userEntity);
    }

    @Override
    public boolean existsUserByEmail(String email) {

        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsUserByDocumentNumber(String documentNumber) {

        return userRepository.findByDocumentNumber(documentNumber).isPresent();
    }
}
