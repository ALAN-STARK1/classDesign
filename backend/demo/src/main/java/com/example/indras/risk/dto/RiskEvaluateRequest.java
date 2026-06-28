package com.example.indras.risk.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluateRequest {

    private String sourceType;
    private Long sourceId;
    private LocalDate date;
}
