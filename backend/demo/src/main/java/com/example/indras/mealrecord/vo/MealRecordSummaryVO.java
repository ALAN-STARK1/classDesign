package com.example.indras.mealrecord.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealRecordSummaryVO {

    private Long id;
    private String mealType;
    private String sourceType;
    private String foodName;
    private BigDecimal amountG;
    private BigDecimal totalCalorie;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalCarbohydrate;
}
