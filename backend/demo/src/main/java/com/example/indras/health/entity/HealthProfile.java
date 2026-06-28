package com.example.indras.health.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "health_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String gender;
    private LocalDate birthday;
    @Column(name = "height_cm")
    private BigDecimal heightCm;
    @Column(name = "weight_kg")
    private BigDecimal weightKg;
    @Column(name = "target_weight_kg")
    private BigDecimal targetWeightKg;
    @Column(name = "activity_level")
    private String activityLevel;
    @Column(name = "health_goal")
    private String healthGoal;
    private BigDecimal bmi;
    @Column(name = "daily_calorie_target")
    private Integer dailyCalorieTarget;
}
