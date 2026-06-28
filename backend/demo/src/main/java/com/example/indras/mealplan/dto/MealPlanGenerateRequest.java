package com.example.indras.mealplan.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanGenerateRequest {

    @jakarta.validation.constraints.NotNull
    private LocalDate planDate;
    private Integer targetCalorie;
    private Long goalCycleId;
}
