package com.example.indras.healthgoal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_goal_cycle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthGoalCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "goal_type")
    private String goalType;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "start_weight_kg")
    private BigDecimal startWeightKg;
    @Column(name = "target_weight_kg")
    private BigDecimal targetWeightKg;
    @Column(name = "target_calorie")
    private Integer targetCalorie;
    @Column(name = "weekly_target_delta_kg")
    private BigDecimal weeklyTargetDeltaKg;
    @Column(name = "progress_percent")
    private BigDecimal progressPercent;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
