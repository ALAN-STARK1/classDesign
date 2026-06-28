package com.example.indras.health.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfileSummaryVO {

    private BigDecimal bmi;
    private String bmiStatus;
    private Integer bmr;
    private Integer tdee;
    private Integer dailyCalorieTarget;
}
