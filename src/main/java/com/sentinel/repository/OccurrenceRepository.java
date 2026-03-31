package com.sentinel.repository;

import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.model.Occurrence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {
    Page<Occurrence> findByStatus(OccurrenceStatus status, Pageable pageable);
}