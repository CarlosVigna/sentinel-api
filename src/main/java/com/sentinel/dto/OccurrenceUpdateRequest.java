package com.sentinel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OccurrenceUpdateRequest {

    @NotNull(message = "Categoria é obrigatória.")
    private Long categoryId;

    @NotNull(message = "Protocolo é obrigatório.")
    private Long protocolId;

    @NotBlank(message = "Placa é obrigatória.")
    private String plate;

    private String description;

    @NotBlank(message = "Texto dos responsáveis é obrigatório.")
    private String textoResponsaveis;

    @NotBlank(message = "Texto do motorista é obrigatório.")
    private String textoMotorista;

    @NotBlank(message = "Texto interno é obrigatório.")
    private String textoInterno;
}