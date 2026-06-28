package com.example.indras.community.dto;

import lombok.*;
import jakarta.validation.constraints.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentCreateRequest {

    @jakarta.validation.constraints.NotBlank
    private String content;
}
