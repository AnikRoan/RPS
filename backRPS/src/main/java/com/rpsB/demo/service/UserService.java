package com.rpsB.demo.service;

import com.rpsB.demo.dto.UserDto;
import com.rpsB.demo.dto.UserUpdateDto;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.mapper.UserMapper;
import com.rpsB.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public String resolvEmail(String email) {
        return userRepository.findEmail(email).orElseThrow(() -> new ApiException("User not found"));
    }

    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDto getMe(Long userId) {
        return userMapper.toDto(userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found")));
    }

    @Transactional
    public UserDto updateMe(UserUpdateDto updateDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("Usr not found"));

        Optional.ofNullable(updateDto.name())
                .ifPresent(user::changeName);
        Optional.ofNullable(updateDto.email())
                .ifPresent(user::changeEmail);
        Optional.ofNullable(updateDto.avatar())
                .ifPresent(user::changeAvatar);
        Optional.ofNullable(updateDto.password())
                .map(passwordEncoder::encode)
                .ifPresent(user::changePassword);

        return userMapper.toDto(user);
    }


    @Transactional
    public String deleteMe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        userRepository.delete(user);
        return "User was deleted";
    }
}
