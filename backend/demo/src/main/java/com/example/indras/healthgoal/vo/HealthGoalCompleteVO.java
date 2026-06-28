package com.example.indras.healthgoal.vo;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthGoalCompleteVO {

    private Long id;
    private String status;
    private String summary;
}
