package com.example.indras.nutrition.vo;

import lombok.*;
import com.example.indras.common.vo.MacroRatioVO;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeNutritionAnalysisVO {

    private Long recipeId;
    private BigDecimal calorie;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
    private MacroRatioVO macroRatio;
}
