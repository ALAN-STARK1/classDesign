package com.example.indras.recipe.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuitabilityScoreVO {

    private Long recipeId;
    private Integer score;
    private Integer calorieScore;
    private Integer macroScore;
    private Integer preferenceScore;
    private Integer riskScore;
    private String reason;
}
