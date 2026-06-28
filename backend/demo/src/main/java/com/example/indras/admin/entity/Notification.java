package com.example.indras.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    private String type;
    private String title;
    private String content;
    @TableField("read_flag")
    private Boolean readFlag;
    @TableField("related_type")
    private String relatedType;
    @TableField("related_id")
    private Long relatedId;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
