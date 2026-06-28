package com.example.indras.mealplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "meal_plan_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "meal_type")
    private String mealType;
    @Column(name = "recipe_id")
    private Long recipeId;
    @Column(name = "recipe_name")
    private String recipeName;
    private BigDecimal calorie;
    @Column(name = "suitability_score")
    private Integer suitabilityScore;
    @Column(name = "recommend_reason")
    private String recommendReason;
    @Column(name = "sort_no")
    private Integer sortNo;
}
