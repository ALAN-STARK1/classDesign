package com.example.indras.mealplan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("meal_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlan {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("plan_date")
    private LocalDate planDate;
    @TableField("target_calorie")
    private Integer targetCalorie;
    @TableField("actual_calorie")
    private BigDecimal actualCalorie;
    private Integer score;
    @TableField("recommend_reason")
    private String recommendReason;
    private String status;
}
