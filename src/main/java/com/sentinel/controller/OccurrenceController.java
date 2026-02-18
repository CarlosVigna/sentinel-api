package com.sentinel.controller;

import com.sentinel.dto.OccurrenceRequest;
import com.sentinel.dto.OccurrenceResponse;
import com.sentinel.dto.OccurrenceUpdateRequest;
import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.service.OccurrenceService;
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
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) OccurrenceStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String plate
    ) {
        return occurrenceService.findAll(page, size, sortBy, direction, status, categoryId, plate);
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

    @PutMapping("/{id}")
public OccurrenceResponse update(
        @PathVariable Long id,
        @Valid @RequestBody OccurrenceUpdateRequest request) {
    return occurrenceService.update(id, request);
}
}
