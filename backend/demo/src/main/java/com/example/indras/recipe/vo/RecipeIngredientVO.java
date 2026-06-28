package com.example.indras.recipe.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientVO {

    private Long ingredientId;
    private String name;
    private BigDecimal amountG;
    private String remark;
}
