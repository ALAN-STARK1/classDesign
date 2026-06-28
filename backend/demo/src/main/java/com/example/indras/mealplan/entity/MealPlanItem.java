package com.example.indras.mealplan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

@TableName("meal_plan_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("plan_id")
    private Long planId;
    @TableField("meal_type")
    private String mealType;
    @TableField("recipe_id")
    private Long recipeId;
    @TableField("recipe_name")
    private String recipeName;
    private BigDecimal calorie;
    @TableField("suitability_score")
    private Integer suitabilityScore;
    @TableField("recommend_reason")
    private String recommendReason;
    @TableField("sort_no")
    private Integer sortNo;
}
