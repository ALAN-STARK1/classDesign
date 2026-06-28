package com.example.indras.recipe.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepDTO {

    private Integer stepNo;
    private String content;
}
