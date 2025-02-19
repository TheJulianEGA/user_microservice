package com.user_microservice.user.domain.api;

import com.user_microservice.user.domain.model.User;



public interface IUserServicePort {

    User registerUser(User user);
}
