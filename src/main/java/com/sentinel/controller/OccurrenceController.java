package com.sentinel.controller;

import com.sentinel.dto.OccurrenceRequest;
import com.sentinel.dto.OccurrenceResponse;
import com.sentinel.dto.OccurrenceUpdateRequest;
import com.sentinel.service.OccurrenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/occurrences")
@RequiredArgsConstructor
public class OccurrenceController {

    private final OccurrenceService occurrenceService;

    @PostMapping
    public OccurrenceResponse create(@Valid @RequestBody OccurrenceRequest request) {
        return occurrenceService.create(request);
    }

    @GetMapping
    public Page<OccurrenceResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        return occurrenceService.findAll(page, size, status);
    }

    @GetMapping("/{id}")
    public OccurrenceResponse findById(@PathVariable Long id) {
        return occurrenceService.findById(id);
    }

    @PutMapping("/{id}")
    public OccurrenceResponse update(
            @PathVariable Long id,
            @Valid @RequestBody OccurrenceUpdateRequest request
    ) {
        return occurrenceService.update(id, request);
    }

    @PatchMapping("/{id}/resolve")
    public OccurrenceResponse resolve(@PathVariable Long id) {
        return occurrenceService.resolve(id);
    }

    @PatchMapping("/{id}/cancel")
    public OccurrenceResponse cancel(@PathVariable Long id) {
        return occurrenceService.cancel(id);
    }

    @PatchMapping("/{id}/reopen")
    public OccurrenceResponse reopen(@PathVariable Long id) {
        return occurrenceService.reopen(id);
    }
}