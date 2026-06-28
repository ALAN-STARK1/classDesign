package com.example.indras.recipe.vo;

import lombok.*;
import com.example.indras.recipe.vo.RecipeIngredientVO;
import com.example.indras.recipe.vo.RecipeStepVO;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailVO {

    private Long id;
    private String name;
    private String description;
    private String category;
    private String difficulty;
    private Integer cookMinutes;
    private Integer servings;
    private BigDecimal totalCalorie;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalCarbohydrate;
    private String status;
    private List<RecipeIngredientVO> ingredients;
    private List<RecipeStepVO> steps;
}
