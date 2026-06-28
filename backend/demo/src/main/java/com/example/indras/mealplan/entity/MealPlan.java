package com.example.indras.mealplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "meal_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "plan_date")
    private LocalDate planDate;
    @Column(name = "target_calorie")
    private Integer targetCalorie;
    @Column(name = "actual_calorie")
    private BigDecimal actualCalorie;
    private Integer score;
    @Column(name = "recommend_reason")
    private String recommendReason;
    private String status;
}
