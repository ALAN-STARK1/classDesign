package com.example.indras.mealplan.vo;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanReplaceLogVO {

    private Long id;
    private Long oldRecipeId;
    private String oldRecipeName;
    private Long newRecipeId;
    private String newRecipeName;
    private String replaceReason;
    private BigDecimal calorieDelta;
    private Integer recommendScore;
    private OffsetDateTime createdAt;
}
