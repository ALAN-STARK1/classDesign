package com.example.indras.mealplan.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanFeedbackRequest {

    @jakarta.validation.constraints.NotBlank
    private String status;
    private BigDecimal actualRatio;
    private String reason;
    private String remark;
    private Boolean createMealRecord;
}
