package com.example.indras.airecipe.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ai_recipe_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ai_recipe_id")
    private Long aiRecipeId;
    @Column(name = "step_no")
    private Integer stepNo;
    private String content;
}
