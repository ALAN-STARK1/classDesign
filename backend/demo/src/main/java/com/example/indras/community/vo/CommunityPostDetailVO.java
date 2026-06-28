package com.example.indras.community.vo;

import lombok.*;
import com.example.indras.common.vo.NutritionVO;
import com.example.indras.community.vo.CommunityCommentVO;
import com.example.indras.community.vo.CommunityPostImageVO;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostDetailVO {

    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String recipeName;
    private String status;
    private String coverImageUrl;
    private Integer imageCount;
    private List<CommunityPostImageVO> images;
    private List<String> tags;
    private NutritionVO nutrition;
    private List<CommunityCommentVO> comments;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Boolean liked;
    private Boolean favorite;
    private String createdAt;
    private String publishedAt;
}
