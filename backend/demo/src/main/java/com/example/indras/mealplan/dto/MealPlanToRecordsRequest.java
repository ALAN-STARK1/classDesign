package com.example.indras.mealplan.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanToRecordsRequest {

    private List<String> mealTypes;
}
