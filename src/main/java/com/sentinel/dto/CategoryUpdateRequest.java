package com.sentinel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequest {

    @NotBlank(message = "Nome da categoria é obrigatório")
    private String name;
}