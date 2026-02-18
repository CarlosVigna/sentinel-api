package com.sentinel.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;
}
