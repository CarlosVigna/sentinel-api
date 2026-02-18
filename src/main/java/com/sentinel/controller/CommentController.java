package com.sentinel.controller;

import com.sentinel.dto.CommentRequest;
import com.sentinel.dto.CommentResponse;
import com.sentinel.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/occurrences/{occurrenceId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse addComment(
            @PathVariable Long occurrenceId,
            @RequestBody CommentRequest request) {
        return commentService.addComment(occurrenceId, request);
    }

    @GetMapping
    public List<CommentResponse> list(
            @PathVariable Long occurrenceId) {
        return commentService.listByOccurrence(occurrenceId);
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
