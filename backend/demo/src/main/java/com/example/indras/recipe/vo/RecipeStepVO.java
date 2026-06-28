package com.example.indras.recipe.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepVO {

    private Integer stepNo;
    private String content;
}
