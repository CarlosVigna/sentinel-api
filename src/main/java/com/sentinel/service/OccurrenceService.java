package com.sentinel.service;

import com.sentinel.dto.OccurrenceRequest;
import com.sentinel.dto.OccurrenceResponse;
import com.sentinel.entity.*;
import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.repository.CategoryRepository;
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
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public OccurrenceResponse create(OccurrenceRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        Occurrence occurrence = Occurrence.builder()
                .category(category)
                .plate(request.getPlate())
                .description(request.getDescription())
                .status(OccurrenceStatus.OPEN)
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .build();

        occurrence.generateTitle();

        occurrenceRepository.save(occurrence);

        return mapToResponse(occurrence);
    }

    public List<OccurrenceResponse> findAll() {
        return occurrenceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OccurrenceResponse resolve(Long id) {

        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada."));

        occurrence.setStatus(OccurrenceStatus.RESOLVED);
        occurrence.setUpdatedAt(LocalDateTime.now());

        occurrenceRepository.save(occurrence);

        return mapToResponse(occurrence);
    }

    public OccurrenceResponse update(Long id, OccurrenceUpdateRequest request) {

    Occurrence occurrence = occurrenceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada."));

    String username = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    boolean isAdmin = user.getRole().name().equals("ADMIN");
    boolean isOwner = occurrence.getCreatedBy().getId().equals(user.getId());

    // ===== REGRA V2 =====
    if (!isAdmin) {

        if (!isOwner) {
            throw new RuntimeException("Você não pode editar ocorrência de outro usuário.");
        }

        if (occurrence.getStatus() == OccurrenceStatus.RESOLVED ||
            occurrence.getStatus() == OccurrenceStatus.CANCELED) {
            throw new RuntimeException("Não é possível editar ocorrência finalizada.");
        }
    }

    if (request.getCategoryId() != null) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));
        occurrence.setCategory(category);
    }

    if (request.getPlate() != null) {
        occurrence.setPlate(request.getPlate());
    }

    if (request.getDescription() != null) {
        occurrence.setDescription(request.getDescription());
    }

    occurrence.generateTitle();
    occurrence.setUpdatedAt(LocalDateTime.now());

    occurrenceRepository.save(occurrence);

    return mapToResponse(occurrence);
}


    private OccurrenceResponse mapToResponse(Occurrence occurrence) {
        return OccurrenceResponse.builder()
                .id(occurrence.getId())
                .title(occurrence.getTitle())
                .plate(occurrence.getPlate())
                .category(occurrence.getCategory().getName())
                .status(occurrence.getStatus())
                .description(occurrence.getDescription())
                .createdBy(occurrence.getCreatedBy().getEmail())
                .createdAt(occurrence.getCreatedAt())
                .build();
    }
}
