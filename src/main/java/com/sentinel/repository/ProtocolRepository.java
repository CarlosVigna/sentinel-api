package com.sentinel.repository;

import com.sentinel.model.Protocol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProtocolRepository extends JpaRepository<Protocol, Long> {

    List<Protocol> findByActiveTrue();

    List<Protocol> findByCategory_IdAndActiveTrue(Long categoryId);

    Optional<Protocol> findByNameIgnoreCaseAndCategory_Id(String name, Long categoryId);

    boolean existsByCategory_Id(Long categoryId);
}