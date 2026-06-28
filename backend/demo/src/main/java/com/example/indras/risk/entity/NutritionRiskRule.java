package com.example.indras.risk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "nutrition_risk_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionRiskRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rule_code")
    private String ruleCode;
    @Column(name = "rule_name")
    private String ruleName;
    private String scenario;
    private String nutrient;
    private String operator;
    @Column(name = "threshold_min")
    private BigDecimal thresholdMin;
    @Column(name = "threshold_max")
    private BigDecimal thresholdMax;
    private String severity;
    private String message;
    private String status;
}
