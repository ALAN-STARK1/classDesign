package com.example.indras.recipe.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recipe_ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "recipe_id")
    private Long recipeId;
    @Column(name = "ingredient_id")
    private Long ingredientId;
    @Column(name = "amount_g")
    private BigDecimal amountG;
    private String remark;
}
