package com.example.indras.report.dto;

import lombok.*;
import jakarta.validation.constraints.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportRequest {

    @jakarta.validation.constraints.NotBlank
    private String month;
}
