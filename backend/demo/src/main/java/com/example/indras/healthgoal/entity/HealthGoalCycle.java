package com.example.indras.healthgoal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("health_goal_cycle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthGoalCycle {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("goal_type")
    private String goalType;
    @TableField("start_date")
    private LocalDate startDate;
    @TableField("end_date")
    private LocalDate endDate;
    @TableField("start_weight_kg")
    private BigDecimal startWeightKg;
    @TableField("target_weight_kg")
    private BigDecimal targetWeightKg;
    @TableField("target_calorie")
    private Integer targetCalorie;
    @TableField("weekly_target_delta_kg")
    private BigDecimal weeklyTargetDeltaKg;
    @TableField("progress_percent")
    private BigDecimal progressPercent;
    private String status;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
