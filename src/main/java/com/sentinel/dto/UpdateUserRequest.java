package com.sentinel.dto;

import com.sentinel.enums.Role;

public class UpdateUserRequest {

    private String nome;
    private String email;
    private Role role;

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}