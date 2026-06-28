package com.example.indras.recipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("recipe_suitability_score")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSuitabilityScore {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("recipe_id")
    private Long recipeId;
    private Integer score;
    @TableField("calorie_score")
    private Integer calorieScore;
    @TableField("macro_score")
    private Integer macroScore;
    @TableField("preference_score")
    private Integer preferenceScore;
    @TableField("risk_score")
    private Integer riskScore;
    private String reason;
    @TableField("calculated_at")
    private LocalDateTime calculatedAt;
}
