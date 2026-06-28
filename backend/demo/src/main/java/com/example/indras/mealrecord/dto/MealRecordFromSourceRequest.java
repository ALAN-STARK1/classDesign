package com.example.indras.mealrecord.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealRecordFromSourceRequest {

    @jakarta.validation.constraints.NotNull
    private LocalDate recordDate;
    @jakarta.validation.constraints.NotBlank
    private String mealType;
    private BigDecimal servingRatio;
}
