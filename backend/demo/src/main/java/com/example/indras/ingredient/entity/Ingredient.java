package com.example.indras.ingredient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

@TableName("ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String unit;
    private BigDecimal calorie;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
    private BigDecimal sodium;
    private String status;
}
