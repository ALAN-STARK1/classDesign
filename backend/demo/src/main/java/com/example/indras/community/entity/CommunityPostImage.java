package com.example.indras.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_post_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "post_id")
    private Long postId;
    @Column(name = "storage_key")
    private String storageKey;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "sort_no")
    private Integer sortNo;
    private Integer width;
    private Integer height;
    @Column(name = "file_size")
    private Long fileSize;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
