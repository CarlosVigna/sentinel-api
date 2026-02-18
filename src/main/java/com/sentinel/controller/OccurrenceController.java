package com.sentinel.controller;

import com.sentinel.dto.OccurrenceRequest;
import com.sentinel.dto.OccurrenceResponse;
import com.sentinel.service.OccurrenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/occurrences")
@RequiredArgsConstructor
public class OccurrenceController {

    private final OccurrenceService occurrenceService;

    @PostMapping
    public OccurrenceResponse create(@RequestBody OccurrenceRequest request) {
        return occurrenceService.create(request);
    }

    @GetMapping
    public List<OccurrenceResponse> findAll() {
        return occurrenceService.findAll();
    }

    @PatchMapping("/{id}/resolve")
    public OccurrenceResponse resolve(@PathVariable Long id) {
        return occurrenceService.resolve(id);
    }

    @PutMapping("/{id}")
public OccurrenceResponse update(
        @PathVariable Long id,
        @RequestBody OccurrenceUpdateRequest request) {
    return occurrenceService.update(id, request);
}

}
