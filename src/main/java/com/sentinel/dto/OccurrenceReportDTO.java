package com.sentinel.dto;

import java.time.LocalDateTime;

public record OccurrenceReportDTO(
        Long id,
        String category,
        String plate,
        String status,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String resolvedBy,
        LocalDateTime resolvedAt,
        String canceledBy,
        LocalDateTime canceledAt
) {}