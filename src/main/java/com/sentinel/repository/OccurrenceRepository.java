package com.sentinel.repository;

import com.sentinel.entity.Occurrence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {
}
