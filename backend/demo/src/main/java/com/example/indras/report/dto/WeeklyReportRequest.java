package com.example.indras.report.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyReportRequest {

    @jakarta.validation.constraints.NotNull
    private LocalDate startDate;
    @jakarta.validation.constraints.NotNull
    private LocalDate endDate;
}
