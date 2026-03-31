package com.sentinel.dto;

import com.sentinel.enums.Role;
import com.sentinel.model.User;

public class UserResponse {

    private String id;
    private String nome;
    private String email;
    private Role role;

    public UserResponse(User user) {
        this.id = user.getId().toString();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}