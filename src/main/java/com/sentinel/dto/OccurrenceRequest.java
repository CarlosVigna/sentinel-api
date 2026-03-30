package com.sentinel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OccurrenceRequest {

    @NotNull(message = "categoryId é obrigatório")
    private Long categoryId;

    @NotNull(message = "protocolId é obrigatório")
    private Long protocolId;

    @NotBlank(message = "plate é obrigatória")
    private String plate;

    private String description;

    @NotBlank(message = "textoResponsaveis é obrigatório")
    private String textoResponsaveis;

    @NotBlank(message = "textoMotorista é obrigatório")
    private String textoMotorista;

    @NotBlank(message = "textoInterno é obrigatório")
    private String textoInterno;
}