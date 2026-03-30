package com.sentinel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProtocolRequest {

    @NotBlank(message = "Nome do protocolo é obrigatório")
    private String name;

    @NotNull(message = "categoryId é obrigatório")
    private Long categoryId;

    @NotBlank(message = "textoResponsaveis é obrigatório")
    private String textoResponsaveis;

    @NotBlank(message = "textoMotorista é obrigatório")
    private String textoMotorista;

    @NotBlank(message = "textoInterno é obrigatório")
    private String textoInterno;

    @Valid
    private List<ProtocolFieldRequest> fields;
}