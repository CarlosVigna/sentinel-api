package com.sentinel.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "protocols")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Protocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String textoResponsaveis;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String textoMotorista;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String textoInterno;

    @OneToMany(
            mappedBy = "protocol",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProtocolField> fields = new ArrayList<>();
}