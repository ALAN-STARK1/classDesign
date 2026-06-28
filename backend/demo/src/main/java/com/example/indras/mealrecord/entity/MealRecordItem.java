package com.example.indras.mealrecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

@TableName("meal_record_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRecordItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("meal_record_id")
    private Long mealRecordId;
    @TableField("food_name")
    private String foodName;
    @TableField("amount_g")
    private BigDecimal amountG;
    private BigDecimal calorie;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
}
