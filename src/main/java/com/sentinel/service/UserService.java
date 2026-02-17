package com.sentinel.service;

import com.sentinel.model.User;
import com.sentinel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {

        return userRepository.save(user);
    }
}
