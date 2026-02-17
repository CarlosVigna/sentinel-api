package com.sentinel.dto;

import com.sentinel.enums.Role;

public class RegisterRequest {

    private String nome;
    private String email;
    private String senha;
    private Role role;

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public Role getRole() {
        return role;
    }
}
