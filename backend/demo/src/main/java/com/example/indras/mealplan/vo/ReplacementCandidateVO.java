package com.example.indras.mealplan.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplacementCandidateVO {

    private Long recipeId;
    private String recipeName;
    private String mealType;
    private BigDecimal calorie;
    private Integer suitabilityScore;
    private Integer recommendScore;
    private String recommendReason;
}
