package com.rpsB.demo.service;

import com.rpsB.demo.entity.User;
import com.rpsB.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public String resolvEmail(String email) {
        return userRepository.findEmail(email).orElseThrow(() -> new ApiException("User not found"));
    }

    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
