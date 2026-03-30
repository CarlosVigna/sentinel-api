package com.sentinel.controller;

import com.sentinel.dto.ProtocolRequest;
import com.sentinel.dto.ProtocolResponse;
import com.sentinel.service.ProtocolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protocols")
@RequiredArgsConstructor
public class ProtocolController {

    private final ProtocolService protocolService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProtocolResponse create(@Valid @RequestBody ProtocolRequest request) {
        return protocolService.create(request);
    }

    @GetMapping
    public List<ProtocolResponse> findAll(@RequestParam(required = false) Long categoryId) {
        return protocolService.findAll(categoryId);
    }

    @GetMapping("/{id}")
    public ProtocolResponse findById(@PathVariable Long id) {
        return protocolService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProtocolResponse update(@PathVariable Long id,
                                   @Valid @RequestBody ProtocolRequest request) {
        return protocolService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        protocolService.delete(id);
    }
}