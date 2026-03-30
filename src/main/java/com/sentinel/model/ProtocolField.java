package com.sentinel.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "protocol_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false)
    private Protocol protocol;

    @Column(nullable = false)
    private String fieldKey;

    @Column(nullable = false)
    private String fieldLabel;

    @Column(nullable = false)
    private Boolean required;

    @Column(nullable = false)
    private String fieldType;
}