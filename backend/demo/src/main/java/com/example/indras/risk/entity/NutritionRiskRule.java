package com.example.indras.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

@TableName("nutrition_risk_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionRiskRule {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("rule_code")
    private String ruleCode;
    @TableField("rule_name")
    private String ruleName;
    private String scenario;
    private String nutrient;
    private String operator;
    @TableField("threshold_min")
    private BigDecimal thresholdMin;
    @TableField("threshold_max")
    private BigDecimal thresholdMax;
    private String severity;
    private String message;
    private String status;
}
