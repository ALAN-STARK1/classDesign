package com.example.indras.mealplan.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanCompletionVO {

    private Long planId;
    private Integer completionRate;
    private Integer completedCount;
    private Integer totalCount;
}
