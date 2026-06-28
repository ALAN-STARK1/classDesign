package com.example.indras.risk.vo;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionRiskResultVO {

    private Long id;
    private LocalDate riskDate;
    private String severity;
    private String sourceType;
    private Long sourceId;
    private String message;
    private String suggestion;
}
