package com.sentinel.repository;

import com.sentinel.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByOccurrenceIdOrderByCreatedAtAsc(Long occurrenceId);
}
