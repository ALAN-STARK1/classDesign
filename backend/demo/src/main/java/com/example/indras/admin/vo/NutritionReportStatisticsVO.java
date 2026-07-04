package com.example.indras.admin.vo;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionReportStatisticsVO {

    private Integer totalReports;
    private Integer weeklyCount;
    private Integer monthlyCount;
    private BigDecimal averageScore;
    private BigDecimal userReportRate;
    private BigDecimal riskTriggerRate;
    private List<TopRiskVO> topRisks;
    private List<DailyReportTrendVO> dailyReportTrend;

    private Integer weeklyReportCount;
    private Integer monthlyReportCount;
    private Integer activeUserCount;
    private BigDecimal avgCompletionRate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopRiskVO {
        private Long ruleId;
        private String ruleName;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyReportTrendVO {
        private String date;
        private Long count;
    }
}
