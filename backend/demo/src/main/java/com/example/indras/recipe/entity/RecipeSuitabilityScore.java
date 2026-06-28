package com.example.indras.recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipe_suitability_score")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSuitabilityScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "recipe_id")
    private Long recipeId;
    private Integer score;
    @Column(name = "calorie_score")
    private Integer calorieScore;
    @Column(name = "macro_score")
    private Integer macroScore;
    @Column(name = "preference_score")
    private Integer preferenceScore;
    @Column(name = "risk_score")
    private Integer riskScore;
    private String reason;
    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;
}
