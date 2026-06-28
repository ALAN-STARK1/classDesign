package com.example.indras.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("community_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityComment {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("post_id")
    private Long postId;
    @TableField("user_id")
    private Long userId;
    private String content;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
