package com.sentinel.service;

import com.sentinel.dto.*;
import com.sentinel.model.User;
import com.sentinel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    public UserResponse create(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User(
                request.getNome(),
                request.getEmail(),
                passwordEncoder.encode(request.getSenha()),
                request.getRole()
        );

        userRepository.save(user);

        return new UserResponse(user);
    }

    public UserResponse getMe(User user) {
        return new UserResponse(user);
    }

    public void changePassword(User user, ChangePasswordRequest request) {

        if (!passwordEncoder.matches(request.getSenhaAtual(), user.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        user.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        userRepository.save(user);
    }

    public UserResponse update(String id, UpdateUserRequest request) {
        User user = userRepository.findById(java.util.UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            throw new RuntimeException("Email já cadastrado");
        }

        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        userRepository.save(user);

        return new UserResponse(user);
    }
}