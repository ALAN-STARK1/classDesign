package com.example.indras.airecipe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("ai_call_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCallLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    private String scene;
    @TableField("request_summary")
    private String requestSummary;
    @TableField("elapsed_ms")
    private Integer elapsedMs;
    private Boolean success;
    @TableField("error_message")
    private String errorMessage;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
