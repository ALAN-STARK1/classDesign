package com.example.indras.nutrition.vo;

import lombok.*;
import com.example.indras.common.vo.MacroRatioVO;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayNutritionVO {

    private LocalDate date;
    private Integer targetCalorie;
    private BigDecimal totalCalorie;
    private Integer caloriePercent;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
    private MacroRatioVO macroRatio;
}
