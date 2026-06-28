package com.example.indras.mealplan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("meal_plan_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanFeedback {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("plan_id")
    private Long planId;
    @TableField("plan_item_id")
    private Long planItemId;
    @TableField("user_id")
    private Long userId;
    @TableField("meal_type")
    private String mealType;
    @TableField("feedback_date")
    private LocalDate feedbackDate;
    private String status;
    @TableField("actual_ratio")
    private BigDecimal actualRatio;
    private String reason;
    private String remark;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
