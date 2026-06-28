package com.example.indras.community.vo;

import lombok.*;
import com.example.indras.common.vo.NutritionVO;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostListItemVO {

    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String recipeName;
    private String status;
    private String coverImageUrl;
    private Integer imageCount;
    private List<String> tags;
    private NutritionVO nutrition;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Boolean liked;
    private Boolean favorite;
    private String createdAt;
    private String publishedAt;
}
