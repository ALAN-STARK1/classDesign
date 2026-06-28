package com.example.indras.healthgoal.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthGoalCycleCreateRequest {

    @jakarta.validation.constraints.NotBlank
    private String goalType;
    @jakarta.validation.constraints.NotNull
    private LocalDate startDate;
    @jakarta.validation.constraints.NotNull
    private LocalDate endDate;
    @jakarta.validation.constraints.NotNull
    private BigDecimal startWeightKg;
    @jakarta.validation.constraints.NotNull
    private BigDecimal targetWeightKg;
    @jakarta.validation.constraints.NotNull
    private Integer targetCalorie;
}
