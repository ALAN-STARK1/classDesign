package com.example.indras.mealrecord.vo;

import lombok.*;
import com.example.indras.common.vo.NutritionVO;
import com.example.indras.mealrecord.vo.MealRecordSummaryVO;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMealRecordVO {

    private LocalDate date;
    private List<MealRecordSummaryVO> records;
    private NutritionVO summary;
}
