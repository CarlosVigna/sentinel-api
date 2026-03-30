package com.sentinel.service;

import com.sentinel.dto.*;
import com.sentinel.exception.BusinessException;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.model.Category;
import com.sentinel.model.Protocol;
import com.sentinel.model.ProtocolField;
import com.sentinel.repository.CategoryRepository;
import com.sentinel.repository.ProtocolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProtocolService {

    private final ProtocolRepository protocolRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProtocolResponse create(ProtocolRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        if (category.getActive() != null && !category.getActive()) {
            throw new BusinessException("Não é possível vincular protocolo a uma categoria inativa.");
        }

        String normalizedName = request.getName().trim();

        protocolRepository.findByNameIgnoreCaseAndCategory_Id(normalizedName, request.getCategoryId())
                .ifPresent(existing -> {
                    if (Boolean.TRUE.equals(existing.getActive())) {
                        throw new BusinessException("Já existe um protocolo ativo com esse nome nesta categoria.");
                    }
                });

        validateFields(request.getFields());

        Protocol protocol = Protocol.builder()
                .name(normalizedName)
                .category(category)
                .active(true)
                .textoResponsaveis(request.getTextoResponsaveis().trim())
                .textoMotorista(request.getTextoMotorista().trim())
                .textoInterno(request.getTextoInterno().trim())
                .fields(new ArrayList<>())
                .build();

        if (request.getFields() != null) {
            for (ProtocolFieldRequest fieldRequest : request.getFields()) {
                ProtocolField field = ProtocolField.builder()
                        .protocol(protocol)
                        .fieldKey(fieldRequest.getFieldKey().trim())
                        .fieldLabel(fieldRequest.getFieldLabel().trim())
                        .required(fieldRequest.getRequired())
                        .fieldType(fieldRequest.getFieldType().trim())
                        .build();

                protocol.getFields().add(field);
            }
        }

        return toResponse(protocolRepository.save(protocol));
    }

    @Transactional(readOnly = true)
    public List<ProtocolResponse> findAll(Long categoryId) {
        List<Protocol> protocols;

        if (categoryId != null) {
            protocols = protocolRepository.findByCategory_IdAndActiveTrue(categoryId);
        } else {
            protocols = protocolRepository.findByActiveTrue();
        }

        return protocols.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProtocolResponse findById(Long id) {
        Protocol protocol = protocolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado."));

        return toResponse(protocol);
    }

    @Transactional
    public ProtocolResponse update(Long id, ProtocolRequest request) {
        Protocol protocol = protocolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado."));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        if (category.getActive() != null && !category.getActive()) {
            throw new BusinessException("Não é possível vincular protocolo a uma categoria inativa.");
        }

        String normalizedName = request.getName().trim();

        protocolRepository.findByNameIgnoreCaseAndCategory_Id(normalizedName, request.getCategoryId())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id) && Boolean.TRUE.equals(existing.getActive())) {
                        throw new BusinessException("Já existe um protocolo ativo com esse nome nesta categoria.");
                    }
                });

        validateFields(request.getFields());

        protocol.setName(normalizedName);
        protocol.setCategory(category);
        protocol.setTextoResponsaveis(request.getTextoResponsaveis().trim());
        protocol.setTextoMotorista(request.getTextoMotorista().trim());
        protocol.setTextoInterno(request.getTextoInterno().trim());

        protocol.getFields().clear();

        if (request.getFields() != null) {
            for (ProtocolFieldRequest fieldRequest : request.getFields()) {
                ProtocolField field = ProtocolField.builder()
                        .protocol(protocol)
                        .fieldKey(fieldRequest.getFieldKey().trim())
                        .fieldLabel(fieldRequest.getFieldLabel().trim())
                        .required(fieldRequest.getRequired())
                        .fieldType(fieldRequest.getFieldType().trim())
                        .build();

                protocol.getFields().add(field);
            }
        }

        return toResponse(protocolRepository.save(protocol));
    }

    @Transactional
    public void delete(Long id) {
        Protocol protocol = protocolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado."));

        protocol.setActive(false);
        protocolRepository.save(protocol);
    }

    private void validateFields(List<ProtocolFieldRequest> fields) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Set<String> keys = new HashSet<>();

        for (ProtocolFieldRequest field : fields) {
            String key = field.getFieldKey().trim().toLowerCase();

            if (!keys.add(key)) {
                throw new BusinessException("Não é permitido repetir fieldKey no mesmo protocolo.");
            }
        }
    }

    private ProtocolResponse toResponse(Protocol protocol) {
        return ProtocolResponse.builder()
                .id(protocol.getId())
                .name(protocol.getName())
                .categoryId(protocol.getCategory().getId())
                .categoryName(protocol.getCategory().getName())
                .active(protocol.getActive())
                .textoResponsaveis(protocol.getTextoResponsaveis())
                .textoMotorista(protocol.getTextoMotorista())
                .textoInterno(protocol.getTextoInterno())
                .fields(
                        protocol.getFields().stream()
                                .map(field -> ProtocolFieldResponse.builder()
                                        .id(field.getId())
                                        .fieldKey(field.getFieldKey())
                                        .fieldLabel(field.getFieldLabel())
                                        .required(field.getRequired())
                                        .fieldType(field.getFieldType())
                                        .build())
                                .toList()
                )
                .build();
    }
}