package com.example.indras.recipe.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientItemDTO {

    private Long ingredientId;
    private BigDecimal amountG;
    private String remark;
}
