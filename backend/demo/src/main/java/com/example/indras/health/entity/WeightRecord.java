package com.example.indras.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("weight_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeightRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("record_date")
    private LocalDate recordDate;
    @TableField("weight_kg")
    private BigDecimal weightKg;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
