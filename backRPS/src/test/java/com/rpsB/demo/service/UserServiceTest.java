package com.rpsB.demo.service;

import com.rpsB.demo.dto.UserDto;
import com.rpsB.demo.dto.UserUpdateDto;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.exception.AppException;
import com.rpsB.demo.mapper.UserMapper;
import com.rpsB.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit Test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void loadUserByEmail_shouldReturnUser() {

        String email = "test@mail.com";
        User user = User.builder().build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        User result = userService.loadUserByEmail(email);

        assertEquals(user, result);
    }

    @Test
    void loadUserByEmail_shouldThrowIfNotFound() {

        when(userRepository.findByEmail("bad@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> userService.loadUserByEmail("bad@mail.com"));
    }

    @Test
    void save_shouldReturnSavedUser() {

        User user = User.builder().build();

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.save(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void getMe_shouldReturnDto() {

        Long userId = 1L;

        User user = User.builder().build();
        UserDto dto = new UserDto(1L, "", "", "");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toDto(user))
                .thenReturn(dto);

        UserDto result = userService.getMe(userId);

        assertEquals(dto, result);
    }

    @Test
    void getMe_shouldThrowIfNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> userService.getMe(1L));
    }

    @Test
    void updateMe_shouldThrowIfUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> userService.updateMe(
                        new UserUpdateDto(null, null, null, null),
                        1L
                ));
    }

    @Test
    void updateMe_shouldUpdateFields() {

        Long userId = 1L;

        User user = User.builder().build();
        user.changeName("old");
        user.changeEmail("old@mail.com");
        user.changeAvatar("oldAvatar");
        user.changePassword("oldPass");

        UserUpdateDto updateDto =
                new UserUpdateDto(
                        "newName",
                        "new@mail.com",
                        "1234",
                        "newAvatar"
                );

        UserDto response = new UserDto(1L, "", "", "");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("1234"))
                .thenReturn("encoded");

        when(userMapper.toDto(user))
                .thenReturn(response);

        UserDto result = userService.updateMe(updateDto, userId);

        assertEquals("newName", user.getName());
        assertEquals("new@mail.com", user.getEmail());
        assertEquals("newAvatar", user.getAvatar());
        assertEquals("encoded", user.getPassword());

        verify(passwordEncoder).encode("1234");

        assertEquals(response, result);
    }

    @Test
    void updateMe_shouldNotChangeFieldsIfNull() {

        Long userId = 1L;

        User user = User.builder().build();
        user.changeName("old");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toDto(user))
                .thenReturn(new UserDto(1L, "", "", ""));

        userService.updateMe(
                new UserUpdateDto(null, null, null, null),
                userId
        );

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deleteMe_shouldDeleteUser() {

        Long userId = 1L;

        User user = User.builder().build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        String result = userService.deleteMe(userId);

        verify(userRepository).delete(user);
        assertEquals("User was deleted", result);
    }

    @Test
    void deleteMe_shouldThrowIfNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> userService.deleteMe(1L));
    }
}