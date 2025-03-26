package com.user_microservice.user.infrastructure.persistence.jpa.adapter;

import com.user_microservice.user.domain.model.User;
import com.user_microservice.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.user_microservice.user.infrastructure.persistence.jpa.mapper.IUserEntityMapper;
import com.user_microservice.user.infrastructure.persistence.jpa.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJpaAdapterTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IUserEntityMapper userEntityMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserJpaAdapter userJpaAdapter;


    @Test
    @DisplayName("Should correctly save a user and return the User model")
    void shouldSaveUser_and_ReturnUserModel() {
        User userModel = new User();

        userModel.setPassword("password");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setPassword("encodedPassword");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userEntityMapper.toEntity(userModel)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userEntityMapper.toModel(userEntity)).thenReturn(userModel);

        User result = userJpaAdapter.registerUser(userModel);

        assertNotNull(result);
        assertEquals(userModel, result);
        verify(passwordEncoder, times(1)).encode("password");
        verify(userEntityMapper, times(1)).toEntity(userModel);
        verify(userRepository, times(1)).save(userEntity);
        verify(userEntityMapper, times(1)).toModel(userEntity);
    }

    @Test
    @DisplayName("Should return true if a user exists by email")
    void shouldReturnTrue_when_userExistsByEmail()
    {
        String email = "julian@mail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));

        boolean result = userJpaAdapter.existsUserByEmail(email);

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Exists user by email should return false when user does not exist")
    void shouldReturnFalse_when_userDoesNotExist_by_Email() {
        String email = "julian@mail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = userJpaAdapter.existsUserByEmail(email);

        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Exists user by identification should return true when user exists")
    void shouldReturnTrue_when_UserExistsByDocumentNumber() {
        String documentNumber = "123";

        when(userRepository.findByDocumentNumber(documentNumber)).thenReturn(Optional.of(new UserEntity()));

        boolean result = userJpaAdapter.existsUserByDocumentNumber(documentNumber);

        assertTrue(result);
        verify(userRepository, times(1)).findByDocumentNumber(documentNumber);
    }

    @Test
    @DisplayName("Exists user by identification should return false when user does not exist")
    void shouldReturnFalse_when_UserDoesNotExist_by_DocumentNumber() {
        String documentNumber = "ID123";

        when(userRepository.findByDocumentNumber(documentNumber)).thenReturn(Optional.empty());

        boolean result = userJpaAdapter.existsUserByDocumentNumber(documentNumber);

        assertFalse(result);
        verify(userRepository, times(1)).findByDocumentNumber(documentNumber);
    }
}
