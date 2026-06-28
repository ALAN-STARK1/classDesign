package com.example.indras.community.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostImageVO {

    private Long id;
    private String url;
    private Integer sortNo;
    private Integer width;
    private Integer height;
}
