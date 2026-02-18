package com.sentinel.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OccurrenceRequest {

    @NotNull(message = "Categoria é obrigatória.")
    private Long categoryId;

    @NotBlank(message = "Placa é obrigatória.")
    private String plate;

    private String description;
}
