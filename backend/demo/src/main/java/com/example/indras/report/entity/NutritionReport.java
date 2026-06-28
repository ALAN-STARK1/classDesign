package com.example.indras.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("nutrition_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionReport {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("report_type")
    private String reportType;
    @TableField("start_date")
    private LocalDate startDate;
    @TableField("end_date")
    private LocalDate endDate;
    @TableField("avg_calorie")
    private BigDecimal avgCalorie;
    @TableField("avg_protein")
    private BigDecimal avgProtein;
    @TableField("avg_fat")
    private BigDecimal avgFat;
    @TableField("avg_carbohydrate")
    private BigDecimal avgCarbohydrate;
    @TableField("target_days")
    private Integer targetDays;
    @TableField("record_days")
    private Integer recordDays;
    @TableField("completion_rate")
    private BigDecimal completionRate;
    @TableField("risk_count")
    private Integer riskCount;
    private String summary;
    @TableField("suggestions_json")
    private String suggestionsJson;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
