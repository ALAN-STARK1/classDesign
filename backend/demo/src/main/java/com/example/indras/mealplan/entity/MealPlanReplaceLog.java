package com.example.indras.mealplan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("meal_plan_replace_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanReplaceLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("plan_id")
    private Long planId;
    @TableField("plan_item_id")
    private Long planItemId;
    @TableField("user_id")
    private Long userId;
    @TableField("old_recipe_id")
    private Long oldRecipeId;
    @TableField("new_recipe_id")
    private Long newRecipeId;
    @TableField("replace_reason")
    private String replaceReason;
    @TableField("calorie_delta")
    private BigDecimal calorieDelta;
    @TableField("recommend_score")
    private Integer recommendScore;
    @TableField("recommend_reason")
    private String recommendReason;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
