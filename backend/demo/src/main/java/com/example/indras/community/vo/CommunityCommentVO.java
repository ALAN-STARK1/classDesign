package com.example.indras.community.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentVO {

    private Long id;
    private Long postId;
    private String authorName;
    private String content;
    private String createdAt;
}
