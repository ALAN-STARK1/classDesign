package com.example.indras.risk.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionRiskRuleSaveRequest {

    private String ruleCode;
    private String ruleName;
    private String scenario;
    private String nutrient;
    private String operator;
    private BigDecimal thresholdMin;
    private BigDecimal thresholdMax;
    private String severity;
    private String message;
    private String status;
}
