package com.example.indras.healthgoal.vo;

import lombok.*;
import com.example.indras.healthgoal.vo.WeightTrendPointVO;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthGoalProgressVO {

    private Long cycleId;
    private BigDecimal progressPercent;
    private BigDecimal currentWeightKg;
    private BigDecimal expectedWeightKg;
    private BigDecimal deviationKg;
    private Boolean warning;
    private List<WeightTrendPointVO> trend;
}
