package com.example.indras.admin.vo;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionReportStatisticsVO {

    private Integer weeklyReportCount;
    private Integer monthlyReportCount;
    private Integer activeUserCount;
    private BigDecimal avgCompletionRate;
}
