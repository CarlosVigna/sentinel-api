package com.sentinel.repository;

import com.sentinel.entity.Occurrence;
import com.sentinel.enums.OccurrenceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {

    Page<Occurrence> findByStatus(OccurrenceStatus status, Pageable pageable);

    Page<Occurrence> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Occurrence> findByPlateContainingIgnoreCase(String plate, Pageable pageable);
}
