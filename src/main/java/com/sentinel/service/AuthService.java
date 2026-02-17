package com.sentinel.service;

import com.sentinel.model.User;
import com.sentinel.enums.Role;
import com.sentinel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(String nome, String email, String senha, Role role) {

        User user = new User(
                nome,
                email,
                passwordEncoder.encode(senha),
                Role.OPERATOR
        );

        userRepository.save(user);

        return jwtService.generateToken(user.getEmail());
    }

    public String login(String email, String senha) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, user.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return jwtService.generateToken(user.getEmail());
    }

}
