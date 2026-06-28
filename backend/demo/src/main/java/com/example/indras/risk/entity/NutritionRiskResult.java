package com.example.indras.risk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_risk_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionRiskResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "rule_id")
    private Long ruleId;
    @Column(name = "source_type")
    private String sourceType;
    @Column(name = "source_id")
    private Long sourceId;
    @Column(name = "risk_date")
    private LocalDate riskDate;
    private String severity;
    private String message;
    private String suggestion;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
