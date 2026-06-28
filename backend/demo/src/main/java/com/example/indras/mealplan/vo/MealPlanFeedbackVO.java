package com.example.indras.mealplan.vo;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanFeedbackVO {

    private Long id;
    private Long planId;
    private Long planItemId;
    private String mealType;
    private LocalDate feedbackDate;
    private String status;
    private BigDecimal actualRatio;
    private String reason;
    private String remark;
}
