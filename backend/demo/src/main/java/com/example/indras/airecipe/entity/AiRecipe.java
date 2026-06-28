package com.example.indras.airecipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("ai_recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecipe {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("source_type")
    private String sourceType;
    @TableField("recipe_name")
    private String recipeName;
    private String description;
    @TableField("recognized_foods_json")
    private String recognizedFoodsJson;
    @TableField("ingredients_json")
    private String ingredientsJson;
    @TableField("nutrition_json")
    private String nutritionJson;
    @TableField("suitability_score")
    private Integer suitabilityScore;
    @TableField("suitability_reason")
    private String suitabilityReason;
    @TableField("health_tips_json")
    private String healthTipsJson;
    @TableField("warnings_json")
    private String warningsJson;
    @TableField("raw_response_json")
    private String rawResponseJson;
    @TableField("source_image_url")
    private String sourceImageUrl;
    @TableField("source_image_key")
    private String sourceImageKey;
    private String status;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
