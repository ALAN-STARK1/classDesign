package com.example.indras.nutrition.vo;

import lombok.*;
import com.example.indras.nutrition.vo.DailyNutritionPointVO;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionRangeVO {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyNutritionPointVO> days;
}
