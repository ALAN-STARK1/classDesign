package com.example.indras.recipe.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import com.example.indras.recipe.dto.RecipeStepDTO;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateRequest {

    @jakarta.validation.constraints.NotBlank
    private String name;
    private String description;
    @jakarta.validation.constraints.NotBlank
    private String category;
    private String difficulty;
    private Integer cookMinutes;
    private Integer servings;
    private List<RecipeStepDTO> steps;
}
