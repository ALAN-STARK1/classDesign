package com.example.indras.community.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostFromAiRequest {

    private String title;
    private String content;
    private Boolean useAiSourceImage;
    private List<Long> imageIds;
}
