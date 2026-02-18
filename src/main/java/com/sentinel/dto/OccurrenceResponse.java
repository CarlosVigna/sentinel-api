package com.sentinel.dto;

import com.sentinel.enums.OccurrenceStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OccurrenceResponse {

    private Long id;
    private String title;
    private String plate;
    private String category;
    private OccurrenceStatus status;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
}
