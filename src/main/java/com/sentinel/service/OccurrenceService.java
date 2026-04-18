package com.sentinel.service;

import com.sentinel.dto.OccurrenceReportDTO;
import com.sentinel.dto.OccurrenceRequest;
import com.sentinel.dto.OccurrenceResponse;
import com.sentinel.dto.OccurrenceUpdateRequest;
import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.exception.BusinessException;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.model.Category;
import com.sentinel.model.Occurrence;
import com.sentinel.model.Protocol;
import com.sentinel.model.User;
import com.sentinel.repository.CategoryRepository;
import com.sentinel.repository.OccurrenceRepository;
import com.sentinel.repository.ProtocolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final CategoryRepository categoryRepository;
    private final ProtocolRepository protocolRepository;

    // =========================
    // CREATE
    // =========================
    public OccurrenceResponse create(OccurrenceRequest request) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        Protocol protocol = protocolRepository.findById(request.getProtocolId())
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado."));

        validateCategoryAndProtocol(category, protocol);

        String normalizedPlate = normalizePlate(request.getPlate());

        Occurrence occurrence = Occurrence.builder()
                .status(OccurrenceStatus.OPEN)
                .category(category)
                .protocol(protocol)
                .plate(normalizedPlate)
                .description(trimToNull(request.getDescription()))
                .textoResponsaveis(requiredTrim(request.getTextoResponsaveis(), "Texto dos responsáveis é obrigatório."))
                .textoMotorista(requiredTrim(request.getTextoMotorista(), "Texto do motorista é obrigatório."))
                .textoInterno(requiredTrim(request.getTextoInterno(), "Texto interno é obrigatório."))
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        occurrence.generateTitle();

        Occurrence saved = occurrenceRepository.save(occurrence);
        return toResponse(saved);
    }

    // =========================
    // LISTAGEM PADRÃO
    // =========================
    public Page<OccurrenceResponse> findAll(int page, int size, String status) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<Occurrence> occurrences;

        if (status == null || status.isBlank()) {
            occurrences = occurrenceRepository.findAll(pageable);
        } else {
            OccurrenceStatus occurrenceStatus;
            try {
                occurrenceStatus = OccurrenceStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new BusinessException("Status inválido.");
            }

            occurrences = occurrenceRepository.findByStatus(occurrenceStatus, pageable);
        }

        return occurrences.map(this::toResponse);
    }

    // =========================
    // FIND BY ID
    // =========================
    public OccurrenceResponse findById(Long id) {
        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada."));

        return toResponse(occurrence);
    }

    // =========================
    // UPDATE
    // =========================
    public OccurrenceResponse update(Long id, OccurrenceUpdateRequest request) {
        User user = getAuthenticatedUser();

        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada."));

        if (occurrence.getStatus() == OccurrenceStatus.RESOLVED ||
                occurrence.getStatus() == OccurrenceStatus.CANCELED) {
            throw new BusinessException("Não é possível editar uma ocorrência encerrada.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        Protocol protocol = protocolRepository.findById(request.getProtocolId())
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado."));

        validateCategoryAndProtocol(category, protocol);

        occurrence.setCategory(category);
        occurrence.setProtocol(protocol);
        occurrence.setPlate(normalizePlate(request.getPlate()));
        occurrence.setDescription(trimToNull(request.getDescription()));
        occurrence.setTextoResponsaveis(requiredTrim(request.getTextoResponsaveis(), "Texto dos responsáveis é obrigatório."));
        occurrence.setTextoMotorista(requiredTrim(request.getTextoMotorista(), "Texto do motorista é obrigatório."));
        occurrence.setTextoInterno(requiredTrim(request.getTextoInterno(), "Texto interno é obrigatório."));
        occurrence.setUpdatedAt(LocalDateTime.now());

        occurrence.generateTitle();

        Occurrence saved = occurrenceRepository.save(occurrence);
        return toResponse(saved);
    }

    // =========================
    // RESOLVE
    // =========================
    public OccurrenceResponse resolve(Long id) {
        User user = getAuthenticatedUser();

        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada."));

        if (occurrence.getStatus() == OccurrenceStatus.RESOLVED) {
            throw new BusinessException("Ocorrência já está resolvida.");
        }

        if (occurrence.getStatus() == OccurrenceStatus.CANCELED) {
            throw new BusinessException("Não é possível resolver uma ocorrência cancelada.");
        }

        occurrence.setStatus(OccurrenceStatus.RESOLVED);
        occurrence.setResolvedBy(user);
        occurrence.setResolvedAt(LocalDateTime.now());
        occurrence.setUpdatedAt(LocalDateTime.now());

        Occurrence saved = occurrenceRepository.save(occurrence);
        return toResponse(saved);
    }

    // =========================
    // CANCEL
    // =========================
    public OccurrenceResponse cancel(Long id) {
        User user = getAuthenticatedUser();

        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada."));

        if (occurrence.getStatus() == OccurrenceStatus.CANCELED) {
            throw new BusinessException("Ocorrência já está cancelada.");
        }

        if (occurrence.getStatus() == OccurrenceStatus.RESOLVED) {
            throw new BusinessException("Não é possível cancelar uma ocorrência resolvida.");
        }

        occurrence.setStatus(OccurrenceStatus.CANCELED);
        occurrence.setCanceledBy(user);
        occurrence.setCanceledAt(LocalDateTime.now());
        occurrence.setUpdatedAt(LocalDateTime.now());

        Occurrence saved = occurrenceRepository.save(occurrence);
        return toResponse(saved);
    }

    // =========================
    // REOPEN
    // =========================
    public OccurrenceResponse reopen(Long id) {
        User user = getAuthenticatedUser();

        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada."));

        if (occurrence.getStatus() != OccurrenceStatus.RESOLVED &&
                occurrence.getStatus() != OccurrenceStatus.CANCELED) {
            throw new BusinessException("Só é possível reabrir ocorrências resolvidas ou canceladas.");
        }

        occurrence.setStatus(OccurrenceStatus.OPEN);
        occurrence.setReopenedBy(user);
        occurrence.setReopenedAt(LocalDateTime.now());
        occurrence.setUpdatedAt(LocalDateTime.now());

        occurrence.setResolvedBy(null);
        occurrence.setResolvedAt(null);
        occurrence.setCanceledBy(null);
        occurrence.setCanceledAt(null);

        Occurrence saved = occurrenceRepository.save(occurrence);
        return toResponse(saved);
    }

    // =========================
    // 🔥 REPORT (NOVO)
    // =========================
    public List<OccurrenceReportDTO> getReport(
            String status,
            Long categoryId,
            String plate
    ) {

        List<Occurrence> occurrences = occurrenceRepository.findAll();

        return occurrences.stream()
                .filter(o -> status == null || o.getStatus().name().equalsIgnoreCase(status))
                .filter(o -> categoryId == null ||
                        (o.getCategory() != null && o.getCategory().getId().equals(categoryId)))
                .filter(o -> plate == null ||
                        o.getPlate().toLowerCase().contains(plate.toLowerCase()))
                .map(o -> new OccurrenceReportDTO(
                        o.getId(),
                        o.getCategory() != null ? o.getCategory().getName() : null,
                        o.getPlate(),
                        o.getStatus().name(),
                        o.getCreatedBy() != null ? o.getCreatedBy().getNome() : null,
                        o.getCreatedAt(),
                        o.getUpdatedAt(),
                        o.getResolvedBy() != null ? o.getResolvedBy().getNome() : null,
                        o.getResolvedAt(),
                        o.getCanceledBy() != null ? o.getCanceledBy().getNome() : null,
                        o.getCanceledAt(),
                        o.getDescription()
                ))
                .toList();
    }

    // =========================
    // HELPERS
    // =========================
    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof User user)) {
            throw new BusinessException("Usuário autenticado inválido.");
        }

        return user;
    }

    private void validateCategoryAndProtocol(Category category, Protocol protocol) {
        if (Boolean.FALSE.equals(category.getActive())) {
            throw new BusinessException("A categoria informada está inativa.");
        }

        if (Boolean.FALSE.equals(protocol.getActive())) {
            throw new BusinessException("O protocolo informado está inativo.");
        }

        if (protocol.getCategory() == null || !protocol.getCategory().getId().equals(category.getId())) {
            throw new BusinessException("O protocolo não pertence à categoria informada.");
        }
    }

    private String normalizePlate(String plate) {
        return plate == null ? null : plate.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String requiredTrim(String value, String message) {
        if (value == null || value.trim().isBlank()) {
            throw new BusinessException(message);
        }
        return value.trim();
    }

    private OccurrenceResponse toResponse(Occurrence occurrence) {
        return OccurrenceResponse.builder()
                .id(occurrence.getId())
                .title(occurrence.getTitle())
                .status(occurrence.getStatus().name())
                .categoryId(occurrence.getCategory() != null ? occurrence.getCategory().getId() : null)
                .categoryName(occurrence.getCategory() != null ? occurrence.getCategory().getName() : null)
                .protocolId(occurrence.getProtocol() != null ? occurrence.getProtocol().getId() : null)
                .protocolName(occurrence.getProtocol() != null ? occurrence.getProtocol().getName() : null)
                .plate(occurrence.getPlate())
                .description(occurrence.getDescription())
                .textoResponsaveis(occurrence.getTextoResponsaveis())
                .textoMotorista(occurrence.getTextoMotorista())
                .textoInterno(occurrence.getTextoInterno())
                .createdByName(occurrence.getCreatedBy() != null ? occurrence.getCreatedBy().getNome() : null)
                .createdAt(occurrence.getCreatedAt())
                .updatedAt(occurrence.getUpdatedAt())
                .resolvedByName(occurrence.getResolvedBy() != null ? occurrence.getResolvedBy().getNome() : null)
                .resolvedAt(occurrence.getResolvedAt())
                .canceledByName(occurrence.getCanceledBy() != null ? occurrence.getCanceledBy().getNome() : null)
                .canceledAt(occurrence.getCanceledAt())
                .reopenedByName(occurrence.getReopenedBy() != null ? occurrence.getReopenedBy().getNome() : null)
                .reopenedAt(occurrence.getReopenedAt())
                .build();
    }
}