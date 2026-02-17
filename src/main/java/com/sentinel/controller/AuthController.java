package com.sentinel.controller;

import com.sentinel.dto.LoginRequest;
import com.sentinel.dto.RegisterRequest;
import com.sentinel.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(
                        request.getNome(),
                        request.getEmail(),
                        request.getSenha(),
                        request.getRole()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        String token = authService.login(
                request.getEmail(),
                request.getSenha()
        );

        return ResponseEntity.ok(token);
    }

}
