package com.example.indras.recipe.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeListItemVO {

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
    private Boolean favorite;
    private Integer suitabilityScore;
}
