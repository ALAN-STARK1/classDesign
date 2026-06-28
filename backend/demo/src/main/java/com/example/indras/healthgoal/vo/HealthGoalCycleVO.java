package com.example.indras.healthgoal.vo;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthGoalCycleVO {

    private Long id;
    private String goalType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal startWeightKg;
    private BigDecimal targetWeightKg;
    private Integer targetCalorie;
    private BigDecimal weeklyTargetDeltaKg;
    private BigDecimal progressPercent;
    private String status;
}
