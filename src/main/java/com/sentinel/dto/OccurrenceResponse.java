package com.sentinel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OccurrenceResponse {

    private Long id;
    private String title;
    private String status;

    private Long categoryId;
    private String categoryName;

    private Long protocolId;
    private String protocolName;

    private String plate;
    private String description;

    private String textoResponsaveis;
    private String textoMotorista;
    private String textoInterno;

    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}