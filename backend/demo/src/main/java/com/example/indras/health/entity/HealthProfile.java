package com.example.indras.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("health_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthProfile {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    private String gender;
    private LocalDate birthday;
    @TableField("height_cm")
    private BigDecimal heightCm;
    @TableField("weight_kg")
    private BigDecimal weightKg;
    @TableField("target_weight_kg")
    private BigDecimal targetWeightKg;
    @TableField("activity_level")
    private String activityLevel;
    @TableField("health_goal")
    private String healthGoal;
    private BigDecimal bmi;
    @TableField("daily_calorie_target")
    private Integer dailyCalorieTarget;
}
