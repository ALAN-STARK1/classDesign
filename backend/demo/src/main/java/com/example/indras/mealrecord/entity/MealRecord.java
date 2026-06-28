package com.example.indras.mealrecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("meal_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("record_date")
    private LocalDate recordDate;
    @TableField("meal_type")
    private String mealType;
    @TableField("source_type")
    private String sourceType;
    @TableField("source_id")
    private Long sourceId;
    @TableField("total_calorie")
    private BigDecimal totalCalorie;
    @TableField("total_protein")
    private BigDecimal totalProtein;
    @TableField("total_fat")
    private BigDecimal totalFat;
    @TableField("total_carbohydrate")
    private BigDecimal totalCarbohydrate;
    private String remark;
}
