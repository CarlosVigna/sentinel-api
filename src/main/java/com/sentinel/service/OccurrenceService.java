package com.sentinel.service;

import com.sentinel.dto.OccurrenceRequest;
import com.sentinel.dto.OccurrenceResponse;
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

@Service
@RequiredArgsConstructor
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final CategoryRepository categoryRepository;
    private final ProtocolRepository protocolRepository;
    // private final ShiftService shiftService;

    public OccurrenceResponse create(OccurrenceRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // shiftService.validateActiveShift(user);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        Protocol protocol = protocolRepository.findById(request.getProtocolId())
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado."));

        if (category.getActive() != null && !category.getActive()) {
            throw new BusinessException("A categoria informada está inativa.");
        }

        if (protocol.getActive() != null && !protocol.getActive()) {
            throw new BusinessException("O protocolo informado está inativo.");
        }

        if (!protocol.getCategory().getId().equals(category.getId())) {
            throw new BusinessException("O protocolo não pertence à categoria informada.");
        }

        String normalizedPlate = normalizePlate(request.getPlate());

        Occurrence occurrence = Occurrence.builder()
                .status(OccurrenceStatus.OPEN)
                .category(category)
                .protocol(protocol)
                .plate(normalizedPlate)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .textoResponsaveis(request.getTextoResponsaveis().trim())
                .textoMotorista(request.getTextoMotorista().trim())
                .textoInterno(request.getTextoInterno().trim())
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        occurrence.generateTitle();

        Occurrence saved = occurrenceRepository.save(occurrence);

        return toResponse(saved);
    }

    public Page<OccurrenceResponse> findAll(int page, int size) {
        Page<Occurrence> occurrences = occurrenceRepository.findAll(
                PageRequest.of(page, size)
        );

        return occurrences.map(this::toResponse);
    }

    private String normalizePlate(String plate) {
        return plate == null
                ? null
                : plate.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
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
                .build();
    }
}