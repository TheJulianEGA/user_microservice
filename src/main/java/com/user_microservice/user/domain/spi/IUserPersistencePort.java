package com.user_microservice.user.domain.spi;

import com.user_microservice.user.domain.model.User;

public interface IUserPersistencePort {

    User registerUser(User user);
    boolean existsUserByEmail(String email);
    boolean existsUserByDocumentNumber(String documentNumber);

}
