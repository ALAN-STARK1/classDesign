package com.example.indras.community.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageUploadVO {

    private Long imageId;
    private String imageUrl;
    private Integer width;
    private Integer height;
    private Long fileSize;
    private String status;
}
