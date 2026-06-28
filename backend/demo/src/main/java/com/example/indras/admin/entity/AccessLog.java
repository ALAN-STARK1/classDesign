package com.example.indras.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("access_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    private String method;
    private String path;
    private String ip;
    @TableField("elapsed_ms")
    private Integer elapsedMs;
    @TableField("status_code")
    private Integer statusCode;
    @TableField("biz_code")
    private Integer bizCode;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
