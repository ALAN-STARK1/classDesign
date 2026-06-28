package com.example.indras.airecipe.vo;

import lombok.*;
import com.example.indras.airecipe.vo.AiIngredientVO;
import com.example.indras.airecipe.vo.NutritionEstimateVO;
import com.example.indras.airecipe.vo.RecognizedFoodVO;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecipeVO {

    private Long id;
    private String recipeName;
    private String description;
    private Integer suitabilityScore;
    private String suitabilityReason;
    private NutritionEstimateVO nutritionEstimate;
    private List<AiIngredientVO> ingredients;
    private List<String> cookingSteps;
    private List<String> warnings;
    private List<RecognizedFoodVO> recognizedFoods;
    private String sourceImageUrl;
    private String status;
}
