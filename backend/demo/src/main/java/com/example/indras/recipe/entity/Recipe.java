package com.example.indras.recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String name;
    private String description;
    private String category;
    private String difficulty;
    @Column(name = "cook_minutes")
    private Integer cookMinutes;
    private Integer servings;
    @Column(name = "total_calorie")
    private BigDecimal totalCalorie;
    @Column(name = "total_protein")
    private BigDecimal totalProtein;
    @Column(name = "total_fat")
    private BigDecimal totalFat;
    @Column(name = "total_carbohydrate")
    private BigDecimal totalCarbohydrate;
    private String status;
}
