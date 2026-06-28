package com.example.indras.healthgoal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthGoalCycleUpdateRequest {

    @NotBlank
    private String goalType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private BigDecimal startWeightKg;

    @NotNull
    private BigDecimal targetWeightKg;

    @NotNull
    private Integer targetCalorie;
}
