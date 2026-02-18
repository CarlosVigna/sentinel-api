package com.sentinel.entity;

import com.sentinel.enums.OccurrenceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Occurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OccurrenceStatus status;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String plate;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== MÉTODO PARA GERAR TÍTULO =====
    public void generateTitle() {
        this.title = formatCategory(this.category.getName()) + " - Veículo " + this.plate;
    }

    private String formatCategory(String name) {
        return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
