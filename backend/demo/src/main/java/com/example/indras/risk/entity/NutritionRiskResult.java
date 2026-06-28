package com.example.indras.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("nutrition_risk_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionRiskResult {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("rule_id")
    private Long ruleId;
    @TableField("source_type")
    private String sourceType;
    @TableField("source_id")
    private Long sourceId;
    @TableField("risk_date")
    private LocalDate riskDate;
    private String severity;
    private String message;
    private String suggestion;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
