package com.example.indras.healthgoal.vo;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightTrendPointVO {

    private LocalDate date;
    private BigDecimal weightKg;
}
