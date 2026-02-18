package com.sentinel.specification;

import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.model.Occurrence;
import org.springframework.data.jpa.domain.Specification;

public class OccurrenceSpecification {

    public static Specification<Occurrence> hasStatus(OccurrenceStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Occurrence> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Occurrence> hasPlate(String plate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("plate")),
                        "%" + plate.toLowerCase() + "%"
                );
    }
}
