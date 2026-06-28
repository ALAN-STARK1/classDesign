package com.example.indras.mealplan.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanItemVO {

    private Long id;
    private String mealType;
    private Long recipeId;
    private String recipeName;
    private BigDecimal calorie;
    private Integer suitabilityScore;
    private String recommendReason;
}
