package com.sentinel.service;

import com.sentinel.dto.CommentRequest;
import com.sentinel.dto.CommentResponse;
import com.sentinel.entity.*;
import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.repository.CommentRepository;
import com.sentinel.repository.OccurrenceRepository;
import com.sentinel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    Occurrence occurrence = occurrenceRepository.findById(occurrenceId)
            .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada."));

    // 🔒 BLOQUEIO PARA OCORRÊNCIAS ENCERRADAS
    if (occurrence.getStatus() == OccurrenceStatus.RESOLVED ||
        occurrence.getStatus() == OccurrenceStatus.CANCELED) {

        throw new RuntimeException("Não é possível comentar em ocorrência encerrada.");
    }

    // 🔒 AQUI ENTRARÁ FUTURAMENTE A VALIDAÇÃO DE TURNO
    // if (!turnoAberto(user)) throw new RuntimeException("Turno fechado.");

    Comment comment = Comment.builder()
            .occurrence(occurrence)
            .author(user)
            .content(request.getContent())
            .createdAt(LocalDateTime.now())
            .build();

    // 🔥 Se estava OPEN vira IN_PROGRESS
    if (occurrence.getStatus() == OccurrenceStatus.OPEN) {
        occurrence.setStatus(OccurrenceStatus.IN_PROGRESS);
    }

    occurrence.setUpdatedAt(LocalDateTime.now());

    commentRepository.save(comment);
    occurrenceRepository.save(occurrence);

    return mapToResponse(comment);
}


    public List<CommentResponse> listByOccurrence(Long occurrenceId) {
        return commentRepository.findByOccurrenceIdOrderByCreatedAtAsc(occurrenceId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .author(comment.getAuthor().getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public OccurrenceResponse cancel(Long id) {

    Occurrence occurrence = occurrenceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada."));

    if (occurrence.getStatus() == OccurrenceStatus.RESOLVED) {
        throw new RuntimeException("Não é possível cancelar uma ocorrência resolvida.");
    }

    if (occurrence.getStatus() == OccurrenceStatus.CANCELED) {
        throw new RuntimeException("Ocorrência já está cancelada.");
    }

    occurrence.setStatus(OccurrenceStatus.CANCELED);
    occurrence.setUpdatedAt(LocalDateTime.now());

    occurrenceRepository.save(occurrence);

    return mapToResponse(occurrence);
}

public OccurrenceResponse reopen(Long id) {

    Occurrence occurrence = occurrenceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada."));

    String username = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    if (user.getRole() != Role.ADMIN) {
        throw new RuntimeException("Apenas ADMIN pode reabrir ocorrências.");
    }

    if (occurrence.getStatus() != OccurrenceStatus.RESOLVED &&
        occurrence.getStatus() != OccurrenceStatus.CANCELED) {
        throw new RuntimeException("Apenas ocorrências encerradas podem ser reabertas.");
    }

    occurrence.setStatus(OccurrenceStatus.OPEN);
    occurrence.setUpdatedAt(LocalDateTime.now());

    occurrenceRepository.save(occurrence);

    return mapToResponse(occurrence);
}


}
