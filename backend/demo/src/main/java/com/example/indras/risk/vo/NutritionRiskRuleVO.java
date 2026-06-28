package com.example.indras.risk.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionRiskRuleVO {

    private Long id;
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
