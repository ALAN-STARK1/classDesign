package com.example.indras.recipe.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "recipe_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "recipe_id")
    private Long recipeId;
    @Column(name = "step_no")
    private Integer stepNo;
    private String content;
}
