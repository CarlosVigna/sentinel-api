package com.sentinel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtocolFieldRequest {

    @NotBlank(message = "fieldKey é obrigatório")
    private String fieldKey;

    @NotBlank(message = "fieldLabel é obrigatório")
    private String fieldLabel;

    @NotNull(message = "required é obrigatório")
    private Boolean required;

    @NotBlank(message = "fieldType é obrigatório")
    private String fieldType;
}