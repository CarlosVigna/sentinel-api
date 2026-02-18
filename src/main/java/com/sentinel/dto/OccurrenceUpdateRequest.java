package com.sentinel.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OccurrenceUpdateRequest {

    private Long categoryId;
    private String plate;
    private String description;
}
