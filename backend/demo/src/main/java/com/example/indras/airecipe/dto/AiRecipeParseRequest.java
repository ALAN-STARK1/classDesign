package com.example.indras.airecipe.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecipeParseRequest {

    private String prompt;
}
