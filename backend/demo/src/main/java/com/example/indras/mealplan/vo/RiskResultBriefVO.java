package com.example.indras.mealplan.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskResultBriefVO {

    private String severity;
    private String message;
}
