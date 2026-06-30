package com.example.indras.health.vo;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfileVO {

    private Long id;
    private String gender;
    private LocalDate birthday;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal targetWeightKg;
    private String activityLevel;
    private String healthGoal;
    private BigDecimal bmi;
    private Integer dailyCalorieTarget;
    private List<String> allergens;
    private List<String> restrictions;
    private List<String> chronicDiseases;
}
