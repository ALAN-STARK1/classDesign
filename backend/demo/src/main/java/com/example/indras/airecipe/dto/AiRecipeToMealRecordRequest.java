package com.example.indras.airecipe.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecipeToMealRecordRequest {

    private LocalDate recordDate;
    private String mealType;
    private BigDecimal servingRatio;
}
