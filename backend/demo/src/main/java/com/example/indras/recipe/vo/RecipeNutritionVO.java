package com.example.indras.recipe.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeNutritionVO {

    private Long recipeId;
    private BigDecimal totalCalorie;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalCarbohydrate;
}
