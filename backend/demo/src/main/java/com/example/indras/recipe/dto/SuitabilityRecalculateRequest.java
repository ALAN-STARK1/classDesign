package com.example.indras.recipe.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuitabilityRecalculateRequest {

    private List<Long> recipeIds;
}
