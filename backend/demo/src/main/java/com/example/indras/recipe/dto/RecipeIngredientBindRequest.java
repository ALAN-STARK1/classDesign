package com.example.indras.recipe.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import com.example.indras.recipe.dto.RecipeIngredientItemDTO;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientBindRequest {

    @jakarta.validation.constraints.NotNull
    private List<RecipeIngredientItemDTO> ingredients;
}
