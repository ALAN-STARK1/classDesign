package com.example.indras.ingredient.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSaveRequest {

    @jakarta.validation.constraints.NotBlank
    private String name;
    @jakarta.validation.constraints.NotBlank
    private String category;
    private String unit;
    private BigDecimal calorie;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
    private BigDecimal sodium;
    private BigDecimal vitaminC;
    private BigDecimal vitaminA;
    private String status;
}
