package com.example.indras.nutrition.vo;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionGapVO {

    private LocalDate date;
    private Integer calorieGap;
    private Integer proteinGap;
    private Integer fatGap;
    private Integer carbohydrateGap;
    private List<String> suggestions;
}
