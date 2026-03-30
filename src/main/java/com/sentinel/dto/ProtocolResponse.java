package com.sentinel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProtocolResponse {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Boolean active;
    private String textoResponsaveis;
    private String textoMotorista;
    private String textoInterno;
    private List<ProtocolFieldResponse> fields;
}