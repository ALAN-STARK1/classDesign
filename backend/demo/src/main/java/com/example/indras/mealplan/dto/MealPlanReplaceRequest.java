package com.example.indras.mealplan.dto;

import lombok.*;
import jakarta.validation.constraints.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanReplaceRequest {

    @jakarta.validation.constraints.NotNull
    private Long newRecipeId;
    private String replaceReason;
    private String remark;
}
