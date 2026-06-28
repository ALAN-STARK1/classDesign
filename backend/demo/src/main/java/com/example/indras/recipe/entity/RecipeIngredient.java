package com.example.indras.recipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

@TableName("recipe_ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("recipe_id")
    private Long recipeId;
    @TableField("ingredient_id")
    private Long ingredientId;
    @TableField("amount_g")
    private BigDecimal amountG;
    private String remark;
}
