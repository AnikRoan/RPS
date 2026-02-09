package com.rpsB.demo.service;

import com.rpsB.demo.dto.UserDto;
import com.rpsB.demo.dto.UserUpdateDto;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.mapper.UserMapper;
import com.rpsB.demo.repository.UserRepository;
import com.rpsB.demo.security.UserPrincipal;
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
    private final AuthService authService;
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

    public UserDto getMe() {
        UserPrincipal principal = authService.getAuthenticatedUserPrincipal();
        return userMapper.toDto(userRepository.findById(principal.getId()).orElseThrow(() ->
                new UsernameNotFoundException("User not found")));
    }

    @Transactional
    public UserDto updateMe(UserUpdateDto updateDto) {
        UserPrincipal principal = authService.getAuthenticatedUserPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow(() ->
                new UsernameNotFoundException("Usr not found"));

        Optional.ofNullable(updateDto.name())
                .ifPresent(user::changeName);
        Optional.ofNullable(updateDto.email())
                .ifPresent(user::changeEmail);
        Optional.ofNullable(updateDto.avatar())
                .ifPresent(user::changeAvatar);
        Optional.ofNullable(passwordEncoder.encode(updateDto.password()))
                .ifPresent(user::changePassword);

        return userMapper.toDto(save(user));
    }

    @Transactional
    public String deleteMe() {
        UserPrincipal principal = authService.getAuthenticatedUserPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        userRepository.delete(user);
        return "User was deleted";
    }
}
