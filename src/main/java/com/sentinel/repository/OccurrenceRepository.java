package com.sentinel.repository;

import com.sentinel.model.Occurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OccurrenceRepository extends 
        JpaRepository<Occurrence, Long>,
        JpaSpecificationExecutor<Occurrence> {
}