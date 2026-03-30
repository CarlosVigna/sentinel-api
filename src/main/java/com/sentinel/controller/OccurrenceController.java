package com.sentinel.controller;

import com.sentinel.dto.OccurrenceRequest;
import com.sentinel.dto.OccurrenceResponse;
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
            @RequestParam(defaultValue = "10") int size
    ) {
        return occurrenceService.findAll(page, size);
    }
}