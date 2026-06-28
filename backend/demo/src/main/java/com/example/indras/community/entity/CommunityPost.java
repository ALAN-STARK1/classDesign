package com.example.indras.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("community_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPost {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    private String title;
    private String content;
    @TableField("recipe_name")
    private String recipeName;
    @TableField("tags_json")
    private String tagsJson;
    @TableField("source_type")
    private String sourceType;
    @TableField("source_id")
    private Long sourceId;
    private String status;
    @TableField("like_count")
    private Integer likeCount;
    @TableField("comment_count")
    private Integer commentCount;
    @TableField("favorite_count")
    private Integer favoriteCount;
    @TableField("published_at")
    private LocalDateTime publishedAt;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
