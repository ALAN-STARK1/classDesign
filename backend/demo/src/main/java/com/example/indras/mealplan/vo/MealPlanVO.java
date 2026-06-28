package com.example.indras.mealplan.vo;

import lombok.*;
import com.example.indras.mealplan.vo.MealPlanItemVO;
import com.example.indras.mealplan.vo.RiskResultBriefVO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanVO {

    private Long id;
    private LocalDate planDate;
    private Integer targetCalorie;
    private BigDecimal actualCalorie;
    private Integer score;
    private String status;
    private String recommendReason;
    private List<RiskResultBriefVO> riskResults;
    private List<MealPlanItemVO> items;
}
