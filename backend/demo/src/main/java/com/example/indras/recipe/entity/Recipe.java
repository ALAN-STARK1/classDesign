package com.example.indras.recipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

@TableName("recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    private String name;
    private String description;
    private String category;
    private String difficulty;
    @TableField("cook_minutes")
    private Integer cookMinutes;
    private Integer servings;
    @TableField("total_calorie")
    private BigDecimal totalCalorie;
    @TableField("total_protein")
    private BigDecimal totalProtein;
    @TableField("total_fat")
    private BigDecimal totalFat;
    @TableField("total_carbohydrate")
    private BigDecimal totalCarbohydrate;
    private String status;
}
