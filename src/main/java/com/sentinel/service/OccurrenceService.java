package com.sentinel.service;

import com.sentinel.dto.OccurrenceRequest;
import com.sentinel.dto.OccurrenceResponse;
import com.sentinel.dto.OccurrenceUpdateRequest;
import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.enums.Role;
import com.sentinel.exception.BusinessException;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.model.Category;
import com.sentinel.model.Occurrence;
import com.sentinel.model.User;
import com.sentinel.repository.CategoryRepository;
import com.sentinel.repository.OccurrenceRepository;
import com.sentinel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.sentinel.specification.OccurrenceSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // ========================= CREATE =========================

    public OccurrenceResponse create(OccurrenceRequest request) {

        User user = getAuthenticatedUser();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        Occurrence occurrence = Occurrence.builder()
                .category(category)
                .plate(normalizePlate(request.getPlate()))
                .description(request.getDescription())
                .status(OccurrenceStatus.OPEN)
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .build();

        occurrence.generateTitle();

        occurrenceRepository.save(occurrence);

        return mapToResponse(occurrence);
    }

    // ========================= LISTAGEM COM PAGINAÇÃO =========================

    public Page<OccurrenceResponse> findAll(
        int page,
        int size,
        String sortBy,
        String direction,
        OccurrenceStatus status,
        Long categoryId,
        String plate
) {

    Sort sort = direction.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() :
            Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);

    Specification<Occurrence> spec = Specification.where(null);

    if (status != null) {
        spec = spec.and(OccurrenceSpecification.hasStatus(status));
    }

    if (categoryId != null) {
        spec = spec.and(OccurrenceSpecification.hasCategory(categoryId));
    }

    if (plate != null && !plate.isBlank()) {
        spec = spec.and(OccurrenceSpecification.hasPlate(plate));
    }

    Page<Occurrence> result = occurrenceRepository.findAll(spec, pageable);

    return result.map(this::mapToResponse);
}

    // ========================= RESOLVE =========================

    public OccurrenceResponse resolve(Long id) {

    Occurrence occurrence = findOccurrenceById(id);
    User user = getAuthenticatedUser();

    occurrence.setStatus(OccurrenceStatus.RESOLVED);
    occurrence.setResolvedBy(user);
    occurrence.setResolvedAt(LocalDateTime.now());
    occurrence.setUpdatedAt(LocalDateTime.now());

    occurrenceRepository.save(occurrence);

    return mapToResponse(occurrence);
}

    // ========================= CANCEL =========================

    public OccurrenceResponse cancel(Long id) {

    Occurrence occurrence = findOccurrenceById(id);
    User user = getAuthenticatedUser();

    if (occurrence.getStatus() == OccurrenceStatus.RESOLVED) {
        throw new BusinessException("Não é possível cancelar uma ocorrência resolvida.");
    }

    if (occurrence.getStatus() == OccurrenceStatus.CANCELED) {
        throw new BusinessException("Ocorrência já está cancelada.");
    }

    occurrence.setStatus(OccurrenceStatus.CANCELED);
    occurrence.setCanceledBy(user);
    occurrence.setCanceledAt(LocalDateTime.now());
    occurrence.setUpdatedAt(LocalDateTime.now());

    occurrenceRepository.save(occurrence);

    return mapToResponse(occurrence);
}

    // ========================= REOPEN (ADMIN ONLY) =========================

    public OccurrenceResponse reopen(Long id) {

    Occurrence occurrence = findOccurrenceById(id);
    User user = getAuthenticatedUser();

    if (user.getRole() != Role.ADMIN) {
        throw new BusinessException("Apenas ADMIN pode reabrir ocorrências.");
    }

    if (occurrence.getStatus() != OccurrenceStatus.RESOLVED &&
            occurrence.getStatus() != OccurrenceStatus.CANCELED) {
        throw new BusinessException("Apenas ocorrências encerradas podem ser reabertas.");
    }

    occurrence.setStatus(OccurrenceStatus.OPEN);
    occurrence.setReopenedBy(user);
    occurrence.setReopenedAt(LocalDateTime.now());
    occurrence.setUpdatedAt(LocalDateTime.now());

    occurrenceRepository.save(occurrence);

    return mapToResponse(occurrence);
}

    // ========================= UPDATE =========================

    public OccurrenceResponse update(Long id, OccurrenceUpdateRequest request) {

        Occurrence occurrence = findOccurrenceById(id);

        User user = getAuthenticatedUser();

        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOwner = occurrence.getCreatedBy().getId().equals(user.getId());

        if (!isAdmin) {

            if (!isOwner) {
                throw new BusinessException("Você não pode editar ocorrência de outro usuário.");
            }

            if (occurrence.getStatus() == OccurrenceStatus.RESOLVED ||
                    occurrence.getStatus() == OccurrenceStatus.CANCELED) {
                throw new BusinessException("Não é possível editar ocorrência finalizada.");
            }
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
            occurrence.setCategory(category);
        }

        if (request.getPlate() != null) {
            occurrence.setPlate(normalizePlate(request.getPlate()));

        }

        if (request.getDescription() != null) {
            occurrence.setDescription(request.getDescription());
        }

        occurrence.generateTitle();
        occurrence.setUpdatedAt(LocalDateTime.now());

        occurrenceRepository.save(occurrence);

        return mapToResponse(occurrence);
    }

    private String normalizePlate(String plate) {

    if (plate == null) {
        return null;
    }

    return plate
            .replaceAll("[^a-zA-Z0-9]", "") // remove tudo que não for letra ou número
            .toUpperCase();
}

    // ========================= MÉTODOS AUXILIARES =========================

    private User getAuthenticatedUser() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    private Occurrence findOccurrenceById(Long id) {
        return occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada."));
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

            // ===== AUDITORIA =====
            .resolvedBy(occurrence.getResolvedBy() != null 
                    ? occurrence.getResolvedBy().getEmail() 
                    : null)
            .resolvedAt(occurrence.getResolvedAt())

            .canceledBy(occurrence.getCanceledBy() != null 
                    ? occurrence.getCanceledBy().getEmail() 
                    : null)
            .canceledAt(occurrence.getCanceledAt())

            .reopenedBy(occurrence.getReopenedBy() != null 
                    ? occurrence.getReopenedBy().getEmail() 
                    : null)
            .reopenedAt(occurrence.getReopenedAt())

            .build();
}
}
