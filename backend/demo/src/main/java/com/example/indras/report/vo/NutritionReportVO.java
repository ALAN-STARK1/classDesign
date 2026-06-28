package com.example.indras.report.vo;

import lombok.*;
import com.example.indras.nutrition.vo.DailyNutritionPointVO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionReportVO {

    private Long id;
    private String reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal avgCalorie;
    private BigDecimal avgProtein;
    private BigDecimal avgFat;
    private BigDecimal avgCarbohydrate;
    private Integer targetDays;
    private Integer recordDays;
    private BigDecimal completionRate;
    private Integer riskCount;
    private String summary;
    private List<String> suggestions;
    private List<DailyNutritionPointVO> trend;
}
