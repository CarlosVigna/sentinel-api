package com.sentinel.dto;

public class ChangePasswordRequest {

    private String senhaAtual;
    private String novaSenha;

    public String getSenhaAtual() { return senhaAtual; }
    public String getNovaSenha() { return novaSenha; }
}