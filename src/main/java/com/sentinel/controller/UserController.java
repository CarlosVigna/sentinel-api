package com.sentinel.controller;

import com.sentinel.dto.ChangePasswordRequest;
import com.sentinel.dto.CreateUserRequest;
import com.sentinel.dto.UpdateUserRequest;
import com.sentinel.dto.UserResponse;
import com.sentinel.model.User;
import com.sentinel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(@RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return userService.getMe(user);
    }

    @PatchMapping("/me/password")
    public void changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(user, request);
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable String id,
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal User loggedUser
    ) {
        System.out.println("UPDATE /users/{id}");
        System.out.println("Usuário logado: " + (loggedUser != null ? loggedUser.getEmail() : "null"));
        System.out.println("Role: " + (loggedUser != null ? loggedUser.getRole() : "null"));

        return userService.update(id, request);
    }
}