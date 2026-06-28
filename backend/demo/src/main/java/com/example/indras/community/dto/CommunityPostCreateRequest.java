package com.example.indras.community.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostCreateRequest {

    @jakarta.validation.constraints.NotBlank
    private String title;
    private String content;
    private String recipeName;
    private List<String> tags;
    @jakarta.validation.constraints.NotEmpty
    private List<Long> imageIds;
}
