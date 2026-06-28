package com.example.indras.mealplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_plan_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "plan_item_id")
    private Long planItemId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "meal_type")
    private String mealType;
    @Column(name = "feedback_date")
    private LocalDate feedbackDate;
    private String status;
    @Column(name = "actual_ratio")
    private BigDecimal actualRatio;
    private String reason;
    private String remark;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
