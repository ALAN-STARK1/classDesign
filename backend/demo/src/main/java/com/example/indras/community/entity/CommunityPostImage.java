package com.example.indras.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("community_post_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostImage {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("post_id")
    private Long postId;
    @TableField("storage_key")
    private String storageKey;
    @TableField("image_url")
    private String imageUrl;
    @TableField("sort_no")
    private Integer sortNo;
    private Integer width;
    private Integer height;
    @TableField("file_size")
    private Long fileSize;
    private String status;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
