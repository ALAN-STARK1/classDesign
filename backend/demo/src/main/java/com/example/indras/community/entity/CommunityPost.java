package com.example.indras.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String title;
    @Lob
    private String content;
    @Column(name = "recipe_name")
    private String recipeName;
    @Column(name = "tags_json")
    private String tagsJson;
    @Column(name = "source_type")
    private String sourceType;
    @Column(name = "source_id")
    private Long sourceId;
    private String status;
    @Column(name = "like_count")
    private Integer likeCount;
    @Column(name = "comment_count")
    private Integer commentCount;
    @Column(name = "favorite_count")
    private Integer favoriteCount;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
