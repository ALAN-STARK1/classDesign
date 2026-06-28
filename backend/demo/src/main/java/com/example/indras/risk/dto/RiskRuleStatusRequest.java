package com.example.indras.risk.dto;

import lombok.*;
import jakarta.validation.constraints.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleStatusRequest {

    @jakarta.validation.constraints.NotBlank
    private String status;
}
