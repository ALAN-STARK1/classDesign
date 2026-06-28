package com.example.indras.mealplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_plan_replace_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanReplaceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "plan_item_id")
    private Long planItemId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "old_recipe_id")
    private Long oldRecipeId;
    @Column(name = "new_recipe_id")
    private Long newRecipeId;
    @Column(name = "replace_reason")
    private String replaceReason;
    @Column(name = "calorie_delta")
    private BigDecimal calorieDelta;
    @Column(name = "recommend_score")
    private Integer recommendScore;
    @Column(name = "recommend_reason")
    private String recommendReason;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
