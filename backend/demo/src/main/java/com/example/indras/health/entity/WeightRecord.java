package com.example.indras.health.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weight_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeightRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "record_date")
    private LocalDate recordDate;
    @Column(name = "weight_kg")
    private BigDecimal weightKg;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
