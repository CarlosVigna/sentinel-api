package com.sentinel.service;

import com.sentinel.dto.CommentRequest;
import com.sentinel.dto.CommentResponse;
import com.sentinel.entity.*;
import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.exception.BusinessException;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.repository.CommentRepository;
import com.sentinel.repository.OccurrenceRepository;
import com.sentinel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final UserRepository userRepository;

    public CommentResponse addComment(Long occurrenceId, CommentRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Occurrence occurrence = occurrenceRepository.findById(occurrenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada."));

        if (occurrence.getStatus() == OccurrenceStatus.RESOLVED ||
                occurrence.getStatus() == OccurrenceStatus.CANCELED) {
            throw new BusinessException("Não é possível comentar em ocorrência encerrada.");
        }

        Comment comment = Comment.builder()
                .occurrence(occurrence)
                .author(user)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        if (occurrence.getStatus() == OccurrenceStatus.OPEN) {
            occurrence.setStatus(OccurrenceStatus.IN_PROGRESS);
        }

        occurrence.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);
        occurrenceRepository.save(occurrence);

        return CommentResponse.builder()
                .id(comment.getId())
                .author(user.getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public List<CommentResponse> listByOccurrence(Long occurrenceId) {

        if (!occurrenceRepository.existsById(occurrenceId)) {
            throw new ResourceNotFoundException("Ocorrência não encontrada.");
        }

        return commentRepository
                .findByOccurrenceIdOrderByCreatedAtAsc(occurrenceId)
                .stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .author(c.getAuthor().getEmail())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();
    }
}
